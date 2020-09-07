package com.brightfield.streams;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@RegisterRestClient
@Path("/v1/base/product")
@Produces("application/json")
public interface ProductRestClient {

    @GET
    @Path("/{product_id}")
    Product getProductById(@PathParam("product_id") Integer id);
}
