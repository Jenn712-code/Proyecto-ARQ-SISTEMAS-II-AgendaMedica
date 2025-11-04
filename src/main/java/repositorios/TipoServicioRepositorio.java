package repositorios;

import entidades.TipoServicio;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TipoServicioRepositorio implements PanacheRepositoryBase<TipoServicio, Integer> { }
