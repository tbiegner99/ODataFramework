package com.tj.dao.hibernate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.tj.dao.filter.AllFilter;
import com.tj.dao.filter.AndFilter;
import com.tj.dao.filter.AnyFilter;
import com.tj.dao.filter.BasicExpression;
import com.tj.dao.filter.BasicFilter;
import com.tj.dao.filter.Expression;
import com.tj.dao.filter.Filter;
import com.tj.dao.filter.FunctionFilter;
import com.tj.dao.filter.MethodExpression;
import com.tj.dao.filter.MethodExpression.Function;
import com.tj.dao.filter.NotFilter;
import com.tj.dao.filter.OrFilter;
import com.tj.dao.filter.PropertyExpression;
import com.tj.dao.filter.Query.Parameter;
import com.tj.dao.filter.ValueExpression;
import com.tj.dao.filter.WhereClause;
import com.tj.exceptions.IllegalOperationException;
import com.tj.exceptions.IllegalRequestException;
import com.tj.producer.util.ReflectionUtil;

public class HibernateWhereClause extends WhereClause {

	public HibernateWhereClause(HibernateQuery<?> owner) {
		super(owner);
	}

	@Override
	public String asString() {
		if (getClausesCollection().isEmpty()) {
			return "";
		}
		String ret = "WHERE ";
		Filter joined=BasicFilter.joinFilters(getClausesWithSecurity());
		ret += filterToString(joined, joined.getClass());
		return ret;
	}

	public String filterToString(Filter f, Class<?> type) {
		if (type == AndFilter.class) {
			Filter lhs = ((AndFilter) f).getLhs();
			Filter rhs = ((AndFilter) f).getRhs();
			return "(" + filterToString(lhs, lhs.getClass()) + ") AND (" + filterToString(rhs, rhs.getClass()) + ") ";
		} else if (type == OrFilter.class) {
			Filter lhs = ((OrFilter) f).getLhs();
			Filter rhs = ((OrFilter) f).getRhs();
			return "(" + filterToString(lhs, lhs.getClass()) + ") OR (" + filterToString(rhs, rhs.getClass()) + ") ";
		} else if (type == NotFilter.class) {
			Filter f2 = ((NotFilter) f).getFilter();
			return "NOT (" + filterToString(f2, f2.getClass()) + ") ";
		} else if (type == FunctionFilter.class) {
			FunctionFilter f2 = ((FunctionFilter) f);
			return boolFuncToString(f2);
		} else if (type == AnyFilter.class) {
			AnyFilter f2 = ((AnyFilter) f);
			return "EXISTS ( from c." + expressionToString(f2.getSource()) + " AS " + f2.getVariable() + " WHERE "
					+ filterToString(f2.getPredicate()) + ")";
		} else if (type == AllFilter.class) { // will return true for empty sets.
			AllFilter f2 = ((AllFilter) f);
			return "NOT EXISTS ( from c." + expressionToString(f2.getSource()) + " AS " + f2.getVariable()
					+ " WHERE NOT(" + filterToString(f2.getPredicate()) + "))";
		} else if (type == BasicFilter.class) {
			BasicFilter f2 = (BasicFilter) f;
			String compare;
			switch (f2.getCompare()) {
				case EQUALS:
					compare = "=";
					break;
				case GE:
					compare = ">=";
					break;
				case GT:
					compare = ">";
					break;
				case LE:
					compare = "<=";
					break;
				case LT:
					compare = "<";
					break;
				case NOT_EQUALS:
					compare = "!=";
					break;
				case NOT_NULL:
					return "(" + expressionToString(f2.getLhs(), f2.getLhs().getClass()) + " IS NOT NULL) ";
				case NULL:
					return "(" + expressionToString(f2.getLhs(), f2.getLhs().getClass()) + " IS  NULL) ";
				default:
					throw new RuntimeException("Usupported operator: " + f2.getCompare());
			}
			//handle special case of enum fields [field] [op] [value]
			String enumExp=checkIfEnum(f2.getLhs(),f2.getRhs(),compare);
			if(enumExp!=null) {return enumExp;}
			return "(" + expressionToString(f2.getLhs(), f2.getLhs().getClass()) + " " + compare + " "
					+ expressionToString(f2.getRhs(), f2.getRhs().getClass()) + ")";
		}
		throw new RuntimeException("Unsupported filter type: " + type.getSimpleName());
	}

	private String checkIfEnum(Expression lhs, Expression rhs, String compare) {
		//check inverse case also to avoid code rewrite, just swap
		if(rhs instanceof PropertyExpression && lhs instanceof ValueExpression) {
			Expression tmp=lhs;
			lhs=rhs;
			rhs=tmp;
		}
		if(lhs instanceof PropertyExpression && rhs instanceof ValueExpression) {
			Class<?> clazz = getOwner().getEntityType();
			String propName=((PropertyExpression)lhs).getProperty();
			Object value=((ValueExpression) rhs).getValue();
			Field field;
			try {
				field = ReflectionUtil.getFieldForType(clazz, propName);
				if(field.getType().isEnum()) {
					Enum<?> val=ReflectionUtil.getEnumFromValue((Class<Enum>)field.getType(),value);
					String name = "where_" + getParameters().size();
					getParameters().add(new Parameter(name, val));
					return "(" + expressionToString(lhs, lhs.getClass()) + " " + compare + " :"+name+ ")";
				}
			} catch(IllegalArgumentException e) {
				throw new IllegalRequestException("Field "+propName+" is an enum and "+value.toString()+" is not a valid value.",e);
			}catch (NoSuchFieldException e) {
				//will likely throw an sql error, but handle it if later if it does
			}

		}
		return null;
	}

	private String expressionToString(Expression lhs) {
		return expressionToString(lhs, lhs.getClass());
	}

	private String expressionToString(Expression lhs, Class<? extends Expression> class1) {
		if (lhs instanceof BasicExpression) {
			BasicExpression be = ((BasicExpression) lhs);
			String op;
			String lhsStr = expressionToString(be.getLhs(), be.getLhs().getClass());
			String rhsStr = expressionToString(be.getRhs(), be.getRhs().getClass());
			switch (be.getOp()) {
				case ADD:
					op = "+";
					break;
				case DIVIDE:
					op = "/";
					break;
				case MOD:
					return "MOD(" + lhsStr + "," + rhsStr + ")";
				case MULTIPLY:
					op = "*";
					break;
				case POWER:
					throw new IllegalOperationException("POWER NOT SUPPORTED");
				case SUBTRACT:
					op = "-";
					break;
				default:
					throw new IllegalOperationException("OPERATION NOT SUPPORTED: " + be.getOp());

			}
			return "(" + lhsStr + " " + op + " " + rhsStr + ")";
		} else if (lhs instanceof PropertyExpression) {
			//TODO: add validation for property
			return ((PropertyExpression) lhs).getProperty().replace("/", ".");
		} else if (lhs instanceof ValueExpression) {
			String name = "where_" + getParameters().size();
			getParameters().add(new Parameter(name, ((ValueExpression) lhs).getValue()));
			return ":" + name;
		} else if (lhs instanceof MethodExpression) {
			MethodExpression exp = ((MethodExpression) lhs);
			StringBuilder builder = new StringBuilder(functionToString(exp.getName()));
			builder.append("(");
			List<Expression> args = new ArrayList<Expression>(exp.getArguments());
			for (int i = 0; i < args.size(); i++) {
				builder.append(expressionToString(args.get(i), lhs.getClass()));
				if (i < args.size() - 1) {
					builder.append(",");
				}
			}
			builder.append(")");
			return builder.toString();
		}
		throw new IllegalOperationException("Expression NOT SUPPORTED: " + lhs.getClass().getSimpleName());
	}

	private String filterToString(Filter lhs) {
		return filterToString(lhs, lhs.getClass());
	}

	private String boolFuncToString(FunctionFilter func) {
		switch (func.getName()) {
			case ENDSWITH:
				return endsWithToString(func);
			case STARTSWITH:
				return startsWithToString(func);
			case SUBSTRINGOF:
				return substringOfToString(func);
		}
		throw new IllegalOperationException("Function not supported: " + func.getName().name());
	}

	private String substringOfToString(FunctionFilter func) {
		List<Expression> args = new ArrayList<Expression>(func.getArgs());
		String arg1 = expressionToString(args.get(0), args.get(0).getClass());
		String arg2 = expressionToString(args.get(1), args.get(1).getClass());
		return "LOCATE(" + arg1 + "," + arg2 + ") > 0 ";
	}

	private String startsWithToString(FunctionFilter func) {
		List<Expression> args = new ArrayList<Expression>(func.getArgs());
		String arg1 = expressionToString(args.get(0), args.get(0).getClass());
		String arg2 = expressionToString(args.get(1), args.get(1).getClass());
		return "LOCATE(" + arg1 + "," + arg2 + ") = 1 ";
	}

	private String endsWithToString(FunctionFilter func) {
		List<Expression> args = new ArrayList<Expression>(func.getArgs());
		String arg1 = expressionToString(args.get(0), args.get(0).getClass());
		String arg2 = expressionToString(args.get(1), args.get(1).getClass());
		// return "LOCATE("+arg1+","+arg2+") = LENGTH("+arg1+;
		return arg1 + " LIKE CONCAT('%'," + arg2 + ")";
	}

	private String functionToString(Function func) {
		switch (func) {
			case TOLOWER:
				return "LOWER";
			case TOUPPER:
				return "UPPER";
			case INDEXOF:
				return "LOCATE";
			case CEIL:
			case CONCAT:
			case DAY:
			case FLOOR:
			case HOUR:
			case LENGTH:
			case MINUTE:
			case MONTH:
			case REPLACE:
			case ROUND:
			case SECOND:
			case SUBSTRING:
			case TRIM:
			case YEAR:
			default:
				return func.name().toUpperCase();

		}

	}
}
