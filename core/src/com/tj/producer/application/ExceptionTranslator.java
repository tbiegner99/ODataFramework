package com.tj.producer.application;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriBuilderException;

import org.hibernate.PropertyAccessException;
import org.odata4j.producer.resources.ExceptionMappingProvider;

import com.sun.jersey.api.NotFoundException;
import com.tj.exceptions.GenericOdataProducerException;
import com.tj.exceptions.IllegalRequestException;
import com.tj.exceptions.NoSuchPropertyException;
import com.tj.exceptions.PropertyError;

/***
<<<<<<< HEAD
 * Better exception handling than the default odata4j application. The responses are more informative and response codes
 * are more indicative of the error.
 * 
=======
 * Better exception handling than the default odata4j application. The responses are more informative and response codes are more
 * indicative of the error.
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
 * @author tbiegner
 *
 */
public class ExceptionTranslator extends ExceptionMappingProvider {

	@Override
	public Response toResponse(RuntimeException e) {
<<<<<<< HEAD
		// by logging here, we will not clutter the code
		if (e instanceof NotFoundException) {
			return super.toResponse(new com.tj.exceptions.NotFoundException("Invalid url request"));
		} else if (e instanceof NoSuchPropertyException) {
			return super.toResponse(new GenericOdataProducerException(Status.BAD_REQUEST, e));
		} else if (e instanceof UriBuilderException) {
			return super
					.toResponse(new GenericOdataProducerException(Status.BAD_REQUEST, "Illegal character in url", e));
		} else if (e instanceof PropertyError) {
			return super.toResponse(new GenericOdataProducerException(Status.INTERNAL_SERVER_ERROR, e));
		} else if (e instanceof WebApplicationException) {
			WebApplicationException webException = ((WebApplicationException) e);
			StatusType status = Status.fromStatusCode(webException.getResponse().getStatus());
			if (status == null) {
				status = new CustomStatus(webException.getResponse().getStatus(), "APPLICATION ERROR ENCOUNTERED");
			}
			return super.toResponse(new GenericOdataProducerException(status, status.getReasonPhrase(), e));
		} else if (e instanceof IllegalArgumentException) {
			return super.toResponse(new IllegalRequestException(e.getMessage(), e));
		} else if (e instanceof PropertyAccessException) {
=======
		//by logging here, we will not clutter the code
		if (e instanceof NotFoundException) {
			return super.toResponse(new com.tj.exceptions.NotFoundException("Invalid url request"));
		} else if(e instanceof NoSuchPropertyException) {
			return super.toResponse(new GenericOdataProducerException(Status.BAD_REQUEST,e));
		} else if(e instanceof UriBuilderException) {
			return super.toResponse(new GenericOdataProducerException(Status.BAD_REQUEST,"Illegal character in url",e));
		} else if(e instanceof PropertyError) {
			return super.toResponse(new GenericOdataProducerException(Status.INTERNAL_SERVER_ERROR,e));
		} else if(e instanceof WebApplicationException) {
			WebApplicationException webException=((WebApplicationException)e);
			StatusType status=Status.fromStatusCode(webException.getResponse().getStatus());
			if(status==null) {
				status=new CustomStatus(webException.getResponse().getStatus(),"APPLICATION ERROR ENCOUNTERED");
			}
			return super.toResponse(new GenericOdataProducerException(status, status.getReasonPhrase(), e));
		} else if(e instanceof IllegalArgumentException) {
			return super.toResponse(new IllegalRequestException(e.getMessage(), e));
		} else if(e instanceof PropertyAccessException) {
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
			return super.toResponse(new IllegalRequestException("Illegal property in request.", e));
		}
		return super.toResponse(e);
	}
<<<<<<< HEAD

	private class CustomStatus implements StatusType {
		int code;
		String message;

		public CustomStatus(int status, String message) {
			this.code = status;
			this.message = message;
=======
	private class CustomStatus implements StatusType {
		int code;
		String message;
		public CustomStatus(int status, String message) {
			this.code=status;
			this.message=message;
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
		}

		@Override
		public int getStatusCode() {
			return code;
		}

		@Override
		public Family getFamily() {
<<<<<<< HEAD
			if (code >= 100 && code < 200) {
				return Family.INFORMATIONAL;
			} else if (code >= 200 && code < 300) {
				return Family.SUCCESSFUL;
			} else if (code >= 300 && code < 400) {
				return Family.REDIRECTION;
			} else if (code >= 400 && code < 500) {
				return Family.CLIENT_ERROR;
			} else if (code >= 500 && code < 600) {
=======
			if(code>=100 && code<200) {
				return Family.INFORMATIONAL;
			} else if(code>=200 && code<300) {
				return Family.SUCCESSFUL;
			} else if(code>=300 && code<400) {
				return Family.REDIRECTION;
			} else if(code>=400 && code<500) {
				return Family.CLIENT_ERROR;
			} else if(code>=500 && code<600) {
>>>>>>> 6ae090a3b6cd5556c0994757c683e9054c98ac23
				return Family.SERVER_ERROR;
			}
			return Family.OTHER;
		}

		@Override
		public String getReasonPhrase() {
			return message;
		}

	}
}
