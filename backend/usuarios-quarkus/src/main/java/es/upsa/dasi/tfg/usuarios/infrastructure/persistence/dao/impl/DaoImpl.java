package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.impl;

import es.upsa.dasi.tfg.common.domain.exceptions.TfgRuntimeException;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.Dao;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.NotificacionRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UsuarioRow;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class DaoImpl implements Dao
{
    private final DataSource dataSource;

    @Inject
    public DaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<UsuarioRow> selectUsuarioByUid(String firebaseUid) {
        final String sql = "SELECT firebase_uid, email, nombre, avatar_url FROM usuarios WHERE firebase_uid = ?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, firebaseUid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UsuarioRow.builder()
                            .firebaseUid(rs.getString("firebase_uid"))
                            .email(rs.getString("email"))
                            .nombre(rs.getString("nombre"))
                            .avatarUrl(rs.getString("avatar_url"))
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public UsuarioRow insertUsuario(UsuarioRow row) {
        final String sql = """
            INSERT INTO usuarios (firebase_uid, email, nombre, avatar_url)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (firebase_uid) DO UPDATE SET email = EXCLUDED.email, nombre = EXCLUDED.nombre, avatar_url = EXCLUDED.avatar_url
            """;
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, row.getFirebaseUid());
            ps.setString(2, row.getEmail());
            ps.setString(3, row.getNombre());
            ps.setString(4, row.getAvatarUrl());
            ps.executeUpdate();
            return row;
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
    }

    @Override
    public List<NotificacionRow> selectNotificacionesByUsuario(String usuarioUid) {
        final String sql = """
            SELECT id, usuario_uid, texto, leida, creado_en
              FROM notificaciones WHERE usuario_uid = ? ORDER BY creado_en DESC
            """;
        List<NotificacionRow> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuarioUid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(NotificacionRow.builder()
                            .id(rs.getLong("id"))
                            .usuarioUid(rs.getString("usuario_uid"))
                            .texto(rs.getString("texto"))
                            .leida(rs.getBoolean("leida"))
                            .creadoEn(rs.getTimestamp("creado_en").toLocalDateTime())
                            .build());
                }
            }
        } catch (SQLException e) { throw new TfgRuntimeException(e); }
        return list;
    }
}
