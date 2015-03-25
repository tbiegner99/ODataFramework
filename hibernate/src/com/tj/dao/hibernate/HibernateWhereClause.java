package com.tj.dao.hibernate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
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
import com.tj.datastructures.VariableScope;
import com.tj.exceptions.IllegalOperationException;
import com.tj.exceptions.IllegalRequestException;
import com.tj.odata.extensions.EdmJavaTypeConverter;
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
		VariableScope<Class<?>> variableScope=new VariableScope<Class<?>>(getOwner().getEntityType());
		ret += filterToString(joined, joined.getClass(),variableScope);
		return ret;
	}

	public String filterToString(Filter f, Class<?> type,VariableScope<Class<?>> scope) {
		if (type == AndFilter.class) {
			Filter lhs = ((AndFilter) f).getLhs();
			Filter rhs = ((AndFilter) f).getRhs();
			return "(" + filterToString(lhs, lhs.getClass(),scope) + ") AND (" + filterToString(rhs, rhs.getClass(),scope) + ") ";
		} else if (type == OrFilter.class) {
			Filter lhs = ((OrFilter) f).getLhs();
			Filter rhs = ((OrFilter) f).getRhs();
			return "(" + filterToString(lhs, lhs.getClass(),scope) + ") OR (" + filterToString(rhs, rhs.getClass(),scope) + ") ";
		} else if (type == NotFilter.class) {
			Filter f2 = ((NotFilter) f).getFilter();
			return "NOT (" + filterToString(f2, f2.getClass(),scope) + ") ";
		} else if (type == FunctionFilter.class) {
			FunctionFilter f2 = ((FunctionFilter) f);
			return boolFuncToString(f2,scope);
		} else if (type == AnyFilter.class) {
			AnyFilter f2 = ((AnyFilter) f);
			VariableScope<Class<?>> subScope=scope.createSubScope();
			if(f2.getSource() instanceof PropertyExpression) {
				subScope.addVariable(f2.getVariable(), getTypeForProperty((PropertyExpression)f2.getSource(),scope));
			}
			return "EXISTS ( from c." + expressionToString(f2.getSource(),subScope) + " AS " + f2.getVariable() + " WHERE "
					+ filterToString(f2.getPredicate(),subScope) + ")";
		} else if (type == AllFilter.class) { // will return true for empty sets.
			AllFilter f2 = ((AllFilter) f);
			VariableScope<Class<?>> subScope=scope.createSubScope();
			if(f2.getSource() instanceof PropertyExpression) {
				subScope.addVariable(f2.getVariable(), getTypeForProperty((PropertyExpression)f2.getSource(),scope));
			}
			return "NOT EXISTS ( from c." + expressionToString(f2.getSource(),subScope) + " AS " + f2.getVariable()
					+ " WHERE NOT(" + filterToString(f2.getPredicate(),subScope) + "))";
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
					return "(" + expressionToString(f2.getLhs(), f2.getLhs().getClass(),scope) + " IS NOT NULL) ";
				case NULL:
					return "(" + expressionToString(f2.getLhs(), f2.getLhs().getClass(),scope) + " IS  NULL) ";
				default:
					throw new RuntimeException("Usupported operator: " + f2.getCompare());
			}
			//handle special case of enum fields [field] [op] [value]
			String enumExp=checkIfEnum(f2.getLhs(),f2.getRhs(),compare,scope);
			if(enumExp!=null) {return enumExp;}
			return "(" + expressionToString(f2.getLhs(), f2.getLhs().getClass(),scope) + " " + compare + " "
					+ expressionToString(f2.getRhs(), f2.getRhs().getClass(),scope) + ")";
		}
		throw new RuntimeException("Unsupported filter type: " + type.getSimpleName());
	}

	private String checkIfEnum(Expression lhs, Expression rhs, String compare,VariableScope<Class<?>> scope) {
		//check inverse case also to avoid code rewrite, just swap
		if(rhs instanceof PropertyExpression && lhs instanceof ValueExpression) {
			Expression tmp=lhs;
			lhs=rhs;
			rhs=tmp;
		}
		//if lhs is property, we should check type compatability and property is valid path
		if(lhs instanceof PropertyExpression && rhs instanceof ValueExpression) {
			Class<?> clazz = getOwner().getEntityType();
			String propName=((PropertyExpression)lhs).getProperty();
			Object value=((ValueExpression) rhs).getValue();
			Class<?> fieldType = getTypeForProperty((PropertyExpression)lhs, scope);
				if(fieldType.isEnum()) {
					try{
						value=ReflectionUtil.getEnumFromValue((Class<Enum>)fieldType,value);

					} catch(IllegalArgumentException e) {
						throw new IllegalRequestException("Field "+propName+" is an enum and "+value.toString()+" is not a valid value.",e);
					}
				} else if(Number.class.isAssignableFrom(fieldType)) {
					value=EdmJavaTypeConverter.convertNumber((Number) value, (Class<Number>) fieldType);
				}
				String name = "where_" + getParameters().size();
				getParameters().add(new Parameter(name, value));
				return "(" + expressionToString(lhs, lhs.getClass(),scope) + " " + compare + " :"+name+ ")";
		}
		return null;
	}

	private String expressionToString(Expression lhs,VariableScope<Class<?>> scope) {
		return expressionToString(lhs, lhs.getClass(),scope);
	}

	private String expressionToString(Expression lhs, Class<? extends Expression> class1,VariableScope<Class<?>> scope) {
		if (lhs instanceof BasicExpression) {
			BasicExpression be = ((BasicExpression) lhs);
			String op;
			String lhsStr = expressionToString(be.getLhs(), be.getLhs().getClass(),scope);
			String rhsStr = expressionToString(be.getRhs(), be.getRhs().getClass(),scope);
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
			getTypeForProperty((PropertyExpression) lhs,scope); //thorws exception if property is invalid
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
				builder.append(expressionToString(args.get(i), lhs.getClass(),scope));
				if (i < args.size() - 1) {
					builder.append(",");
				}
			}
			builder.append(")");
			return builder.toString();
		}
		throw new IllegalOperationException("Expression NOT SUPPORTED: " + lhs.getClass().getSimpleName());
	}

	private Class<?> getTypeForProperty(PropertyExpression lhs,VariableScope<Class<?>> scope) {
		String[] components=lhs.getProperty().split("/");
		int i=0;
		Class<?> startingClass=scope.getDefaultScopeVariable();
		if(scope.isVariableInScope(components[i])) {
			startingClass=scope.getVariable(components[i++]);
		}
		Class<?> containingCollection=null;
		for(;i<components.length;i++) {
			//Collections may have size as last property in chain
			if(i==components.length-1 && containingCollection!=null && components[i].equals("size")) {
				return startingClass;
			}
			try {
				Field f=ReflectionUtil.getFieldForType(startingClass, components[i]);
				startingClass=f.getType();
				if(Collection.class.isAssignableFrom(startingClass)) {
					containingCollection=startingClass;
					startingClass=(Class<?>)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0];
				} else {
					containingCollection=null;
				}
			} catch (NoSuchFieldException e) {
				throw new IllegalRequestException("Property is not valid in path: "+components[i]);
			}
		}
		return startingClass;

	}

	private String filterToString(Filter lhs,VariableScope<Class<?>> scope) {
		return filterToString(lhs, lhs.getClass(),scope);
	}

	private String boolFuncToString(FunctionFilter func,VariableScope<Class<?>> scope) {
		switch (func.getName()) {
			case ENDSWITH:
				return endsWithToString(func,scope);
			case STARTSWITH:
				return startsWithToString(func,scope);
			case SUBSTRINGOF:
				return substringOfToString(func,scope);
		}
		throw new IllegalOperationException("Function not supported: " + func.getName().name());
	}

	private String substringOfToString(FunctionFilter func,VariableScope<Class<?>> scope) {
		List<Expression> args = new ArrayList<Expression>(func.getArgs());
		String arg1 = expressionToString(args.get(0), args.get(0).getClass(),scope);
		String arg2 = expressionToString(args.get(1), args.get(1).getClass(),scope);
		return "LOCATE(" + arg1 + "," + arg2 + ") > 0 ";
	}

	private String startsWithToString(FunctionFilter func,VariableScope<Class<?>> scope) {
		List<Expression> args = new ArrayList<Expression>(func.getArgs());
		String arg1 = expressionToString(args.get(0), args.get(0).getClass(),scope);
		String arg2 = expressionToString(args.get(1), args.get(1).getClass(),scope);
		return "LOCATE(" + arg1 + "," + arg2 + ") = 1 ";
	}

	private String endsWithToString(FunctionFilter func,VariableScope<Class<?>> scope) {
		List<Expression> args = new ArrayList<Expression>(func.getArgs());
		String arg1 = expressionToString(args.get(0), args.get(0).getClass(),scope);
		String arg2 = expressionToString(args.get(1), args.get(1).getClass(),scope);
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
