package co.com.model;

public class Promedio {
    private int idPromedio;
    private int idEstudiante;
    private int idMateria;
    private double nota1;
    private double nota2;
    private double nota3;
    private double promedio;

    // Nuevos campos descriptivos
    private String nombreEstudiante;
    private String nombreMateria;

    public Promedio() {}

    // Constructor completo (con IDs)
    public Promedio(int idPromedio, int idEstudiante, int idMateria,
                    double nota1, double nota2, double nota3, double promedio) {
        this.idPromedio = idPromedio;
        this.idEstudiante = idEstudiante;
        this.idMateria = idMateria;
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
        this.promedio = promedio;
    }

    // 🔹 Nuevo constructor para cuando traes los nombres (JOIN)
    public Promedio(int idPromedio, String nombreEstudiante, String nombreMateria,
                    double nota1, double nota2, double nota3, double promedio) {
        this.idPromedio = idPromedio;
        this.nombreEstudiante = nombreEstudiante;
        this.nombreMateria = nombreMateria;
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
        this.promedio = promedio;
    }

    // Getters y setters
    public int getIdPromedio() { return idPromedio; }
    public void setIdPromedio(int idPromedio) { this.idPromedio = idPromedio; }

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public int getIdMateria() { return idMateria; }
    public void setIdMateria(int idMateria) { this.idMateria = idMateria; }

    public double getNota1() { return nota1; }
    public void setNota1(double nota1) { this.nota1 = nota1; }

    public double getNota2() { return nota2; }
    public void setNota2(double nota2) { this.nota2 = nota2; }

    public double getNota3() { return nota3; }
    public void setNota3(double nota3) { this.nota3 = nota3; }

    public double getPromedio() { return promedio; }
    public void setPromedio(double promedio) { this.promedio = promedio; }

    // 🔹 Nuevos getters/setters
    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getNombreMateria() { return nombreMateria; }
    public void setNombreMateria(String nombreMateria) { this.nombreMateria = nombreMateria; }
}
