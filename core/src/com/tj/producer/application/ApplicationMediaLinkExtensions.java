package com.tj.producer.application;

import java.lang.reflect.Proxy;

import org.odata4j.producer.ODataContext;
import org.odata4j.producer.OMediaLinkExtension;
import org.odata4j.producer.OMediaLinkExtensions;

import com.tj.odata.proxy.ODataProxyFactory;
import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.producer.media.MediaLinkExtension;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.user.User;

public class ApplicationMediaLinkExtensions implements OMediaLinkExtensions {

	private ProducerConfiguration config;
	private CompositeSecurityManager manager;
	private User user;
	private ODataProxyFactory proxy;

	public ApplicationMediaLinkExtensions(ProducerConfiguration cfg, CompositeSecurityManager securityManager,
			User requestUser) {
		config = cfg;
		this.manager = securityManager;
		this.user = requestUser;
	}

	public ApplicationMediaLinkExtensions(ProducerConfiguration cfg, CompositeSecurityManager securityManager,
					User requestUser,ODataProxyFactory factory) {
				config = cfg;
				this.manager = securityManager;
				this.user = requestUser;
				this.proxy=factory;
	}

	@Override
	public OMediaLinkExtension create(ODataContext context) {
		OMediaLinkExtension ret=new MediaLinkExtension(config, manager, user);
		if(proxy!=null) {
			return (OMediaLinkExtension) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{OMediaLinkExtension.class}, proxy.getServiceProxy(ret));
		}
		return ret;
	}

}
