package pe.edu.cibertec.daw_t1_agueroarcestephano.service;

import pe.edu.cibertec.daw_t1_agueroarcestephano.model.Usuario;

import java.util.List;

public interface IUsuarioService {

    Usuario buscarUsuarioXNomUsuario(String nomusuario);
    Usuario guardarUsuario(Usuario usuario);
    void actualizarUsuario(Usuario usuario);
}