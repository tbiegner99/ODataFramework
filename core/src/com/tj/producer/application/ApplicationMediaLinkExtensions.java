package com.tj.producer.application;

import org.odata4j.producer.ODataContext;
import org.odata4j.producer.OMediaLinkExtension;
import org.odata4j.producer.OMediaLinkExtensions;

import com.tj.producer.configuration.ProducerConfiguration;
import com.tj.producer.media.MediaLinkExtension;
import com.tj.security.CompositeSecurityManager;
import com.tj.security.User;

public class ApplicationMediaLinkExtensions implements OMediaLinkExtensions {

	private ProducerConfiguration config;
	private CompositeSecurityManager manager;
	private User user;

	public ApplicationMediaLinkExtensions(ProducerConfiguration cfg, CompositeSecurityManager securityManager,
			User requestUser) {
		config = cfg;
		this.manager = securityManager;
		this.user = requestUser;
	}

	@Override
	public OMediaLinkExtension create(ODataContext context) {
		return new MediaLinkExtension(config, manager, user);
	}

}
