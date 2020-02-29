package org.rnott.service.util;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;

/**
 * Exceptions raised by @Transactional annotated methods are wrapped by Spring in {@link TransactionSystemException}. 
 * Unwrap and attempt to map.
 */
@Provider
@Component
public class TransactionSystemExceptionMapper implements ExceptionMapper<TransactionSystemException> {

	@Inject
    private DefaultExceptionMapper mapper;

    /*
     * (non-Javadoc)
     * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
     */
    @Override
    public Response toResponse( TransactionSystemException exception ) {
        return mapper.buildResponse( exception.getCause() );
    }
}
