package br.com.fiap.gs.repositorio;

import br.com.fiap.gs.entidade.Medicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicaoRepositorio extends JpaRepository<Medicao, Long> {
    List<Medicao> findByTipoSensorOrderByDataHoraDesc(String tipoSensor);
    List<Medicao> findByIdentificadorSensorOrderByDataHoraDesc(String identificadorSensor);
}