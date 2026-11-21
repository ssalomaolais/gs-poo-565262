package br.com.fiap.gs.repositorio;

import br.com.fiap.gs.entidade.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertaRepositorio extends JpaRepository<Alerta, Long> {
    List<Alerta> findByNivelOrderByDataHoraDesc(String nivel);
}