package co.com.manager;

import java.util.ArrayList;
import java.util.List;

import co.com.model.Materia;

public class MateriasManager {
	
	public List<Materia> getTodasLasMateriasSinBd() {
		List<Materia> materias = new ArrayList<Materia>();

		for (int i = 0; i < 3 ; i++) {
			
			Materia mat = new Materia();
			mat.setIdMateria(i);
			mat.setNombre("Materia " + i);
			
			materias.add(mat);
		}
		
		return materias;
	}

}
