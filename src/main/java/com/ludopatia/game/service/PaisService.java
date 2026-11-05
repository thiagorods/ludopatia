
package com.ludopatia.game.service;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.model.dao.PaisDao;
import com.ludopatia.game.model.dao.impl.PaisDaoJDBC;
import com.ludopatia.game.model.entities.Pais;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PaisService {

    public List<Pais> listarPaises() throws SQLException {

        try (Connection connection = ConnectionFactory.getConnection()) {

            PaisDao paisDao = new PaisDaoJDBC(connection);

            return paisDao.listarTodos();
        }
    }

    public Optional<Pais> buscarPais(long id) throws SQLException {

        try (Connection connection = ConnectionFactory.getConnection()) {

            PaisDao paisDao = new PaisDaoJDBC(connection);

            return paisDao.buscarPorId(id);
        }
    }
}
