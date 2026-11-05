
package com.ludopatia.game;

import com.ludopatia.game.infrastructure.database.ConnectionFactory;
import com.ludopatia.game.infrastructure.database.DatabaseInitializer;
import com.ludopatia.game.model.dao.PaisDao;
import com.ludopatia.game.model.dao.impl.PaisDaoJDBC;
import com.ludopatia.game.model.entities.Pais;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TestePaisDao {

    public static void main(String[] args) {

        try {
            DatabaseInitializer.inicializar();

            try (Connection connection = ConnectionFactory.getConnection()) {

                PaisDao paisDao = new PaisDaoJDBC(connection);

                List<Pais> paises = paisDao.listarTodos();

                System.out.println("Total de países: " + paises.size());

                for (Pais pais : paises) {
                    System.out.println(pais.getNome());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
