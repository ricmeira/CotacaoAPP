package Model;

/**
 * Created by ricardo on 9/22/2016.
 */

public class Currency {
    private String nome;
    private double valor;
    private int ultima_consulta;
    private String fonte;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getUltima_consulta() {
        return ultima_consulta;
    }

    public void setUltima_consulta(int ultima_consulta) {
        this.ultima_consulta = ultima_consulta;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }
}
