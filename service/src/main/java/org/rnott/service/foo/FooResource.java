/*
 * Copyright 2016 Randy Nott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rnott.service.foo;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.rnott.service.api.Foo;
import org.rnott.service.util.Field;
import org.rnott.service.util.ClientException;

@Path("foos")
@Produces(APPLICATION_JSON_VALUE)
public class FooResource {

	@Inject
	private FooService service;

	@Inject
	private UriInfo uriInfo;

	@GET
	public Response query() {
		Set<Foo> result = new HashSet<>();
		service.fetchAll().forEach( f -> { 
			result.add( new FooMediator().toBinding( f ) );
		});
		return Response
			.ok( result )
			.build();
	}

	@GET
	@Path("{id}")
	public Response fetch( @PathParam("id") String id, @QueryParam("expand") String expansions ) {
		List<Field> fields = expansions == null ? null : Field.newInstance( expansions ).subfields();
		return Response
			.ok( new FooMediator().toBinding( service.fetch( id, fields ) ) )
			.build();
	}

	@POST
	public Response create( Foo foo ) {
		FooEntity entity = service.create( new FooMediator().toEntity( foo ) );
		return Response
			.created( uriInfo.getRequestUriBuilder().path( entity.getId() ).build() )
			.build();
	}	

	@PUT
	@Path("{id}")
	public Response update( @PathParam("id") String id, Foo foo ) {
		if ( ! id.contentEquals( foo.id ) ) {
			throw new ClientException( "Entity and path ID must match" );
		}
		FooEntity entity = service.fetch( id );
		service.update( new FooMediator().toEntity( foo, entity ) );
		return Response
			.noContent()
			.build();
	}	
}
