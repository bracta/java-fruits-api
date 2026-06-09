package dev.kameshs.fruits.api;

import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.quarkus.panache.common.Sort;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/api/fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitsResource {

  @ConfigProperty(name = "my.fruit")
  String defaultFruit;

  @GET
  @Path("/default")
  public Fruit defaultFruit() {
    return Fruit.findByName(defaultFruit);
  }

  @GET
  @Path("/")
  public List<Fruit> fruits() {
    return Fruit.listAll(Sort.ascending("name,season"));
  }

  // Deliberate fault injection: returns HTTP 500 on demand so Prometheus records a
  // SERVER_ERROR data point (drives the Continuous Verification error-rate metric demo).
  @GET
  @Path("/error")
  public Fruit error() {
    throw new javax.ws.rs.InternalServerErrorException(
        "Simulated server error for CV error-rate demo");
  }

  @GET
  @Path("/season/{season}")
  public List<Fruit> fruitsBySeason(@PathParam("season") String season) {
    return Fruit.fruitsBySeason(season);
  }

  @GET
  @Path("/search/{name}")
  public Fruit fruitsByName(@PathParam("name") String name) {
    return Fruit.findByName(name);
  }

  @POST
  @Path("/add")
  @Transactional
  public Response addFruit(Fruit fruit) {
    fruit.persist();
    return Response
      .status(201)
      .build();
  }

  @DELETE
  @Path("/{id}")
  @Transactional
  public void fruitsBySeason(@PathParam("id") Long id) {
    Fruit fruit = Fruit.findById(id);
    if (fruit == null) {
      throw new NotFoundException();
    }
    fruit.delete();
  }

  @DELETE
  @Path("/")
  @Transactional
  public void deleteAll() {
    Fruit.deleteAll();
  }
}
