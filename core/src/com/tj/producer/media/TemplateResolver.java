package com.tj.producer.media;


public class TemplateResolver implements ReflectionResolver{

	private String template;
	private String variable;
	private ReflectionResolver resolver;

	public TemplateResolver(String template,String variable,ReflectionResolver resolver) {
		this.template=template;
		this.variable=variable;
		this.resolver=resolver;
	}



	@Override
	public Object getValue(Object subject, Object... args) {
		Object replacement=resolver.getValue(subject, args);
		if(variable!=null) {
			return template.replaceAll("\\{\\{"+variable+"\\}\\}", replacement.toString());
		}
		return replacement;
	}

	@Override
	public void setValue(Object entity, Object value) {
		resolver.setValue(entity, value);
	}
}
