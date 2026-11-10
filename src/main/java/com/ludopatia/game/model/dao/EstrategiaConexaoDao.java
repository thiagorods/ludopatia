package com.ludopatia.game.model.dao;

import com.ludopatia.game.model.entities.EstrategiaConexao;

import java.sql.SQLException;
import java.util.List;

public interface EstrategiaConexaoDao {

    List<EstrategiaConexao> listarTodas() throws SQLException;

    List<EstrategiaConexao> listarPorOrigem(long idOrigem)
            throws SQLException;

    List<EstrategiaConexao> listarPorDestino(long idDestino)
            throws SQLException;

    boolean existe(long idOrigem, long idDestino)
            throws SQLException;

    void adicionar(EstrategiaConexao conexao)
            throws SQLException;

    boolean remover(long idOrigem, long idDestino)
            throws SQLException;
}
