package pe.edu.cibertec.daw_t1_agueroarcestephano.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.cibertec.daw_t1_agueroarcestephano.dto.ResultadoDto;
import pe.edu.cibertec.daw_t1_agueroarcestephano.dto.UsuarioDto;
import pe.edu.cibertec.daw_t1_agueroarcestephano.model.Usuario;
import pe.edu.cibertec.daw_t1_agueroarcestephano.service.UsuarioService;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/seguridad")
public class SeguridadController {
    private UsuarioService usuarioService;
    @GetMapping("/usuario")
    public String frmMantUsuario(Model model){
        model.addAttribute("listaUsuarios",
                usuarioService.listarUsuario());
        return "seguridad/formusuario";
    }
    @PostMapping("/usuario")
    @ResponseBody
    public ResultadoDto registrarUsuario(@RequestBody UsuarioDto usuarioDto){
        String mensaje = "Usuario registrado correctamente";
        boolean respuesta = true;
        try {
            Usuario usuario = new Usuario();
            usuario.setNombres(usuarioDto.getNombres());
            usuario.setApellidos(usuarioDto.getApellidos());
            if(usuarioDto.getIdusuario() > 0){
                usuario.setIdusuario(usuarioDto.getIdusuario());
                usuario.setActivo(usuarioDto.getActivo());
                usuarioService.actualizarUsuario(usuario);
            }else{
                usuario.setNomusuario(usuarioDto.getNomusuario());
                usuario.setEmail(usuarioDto.getEmail());
                usuarioService.guardarUsuario(usuario);
            }
        }catch (Exception ex){
            mensaje = "Usuario no registrado, error en la BD";
            respuesta = false;
        }
        return ResultadoDto.builder().mensaje(mensaje).respuesta(respuesta).build();
    }

    @GetMapping("/cambiar-password")
    public String cambiarPassword(Model model) {
        return "seguridad/cambiar_password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam("newPassword") String newPassword,
                                  @RequestParam("confirmNewPassword") String confirmNewPassword,
                                  RedirectAttributes redirectAttributes) {

        if (!validarPassword(newPassword)) {
            redirectAttributes.addFlashAttribute("error", "La contraseña debe ser de mínimo 8 caracteres, al menos una letra mayúscula, una letra minúscula, un número y un carácter especial");
            return "redirect:/seguridad/cambiar-password";
        }

        if (!newPassword.equals(confirmNewPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/seguridad/cambiar-password";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String nomusuario = auth.getName();
        Usuario usuario = usuarioService.buscarUsuarioXNomUsuario(nomusuario);
        usuario.setPassword(encodedPassword);
        usuarioService.actualizarUsuario(usuario);

        redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
        return "redirect:/seguridad/cambiar-password";
    }

    @GetMapping("/usuario/{id}")
    @ResponseBody
    public Usuario frmMantUsuario(@PathVariable("id") int id){
        return usuarioService.buscarUsuarioXIdUsuario(id);
    }
    @GetMapping("/usuario/lista")
    @ResponseBody
    public List<Usuario> listaUsuario(){
        return usuarioService.listarUsuario();
    }


    private boolean validarPassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}$";
        return password.matches(passwordRegex);
    }

}
