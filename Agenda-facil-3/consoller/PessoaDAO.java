import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PessoaDAO {

    public static boolean inserirPessoa(Pessoa pessoa) {
        String sql = "INSERT INTO pessoas(nome, telefone, email) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Erro: Falha na conexão com o banco de dados");
                return false;
            }
            stmt.setString(1, pessoa.getNome());
            stmt.setString(2, pessoa.getTelefone());
            stmt.setString(3, pessoa.getEmail());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir pessoa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<Pessoa> listarPessoas() {
        List<Pessoa> pessoas = new ArrayList<>();
        String sql = "SELECT * FROM pessoas";
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (conn == null) {
                System.err.println("Erro: Falha na conexão com o banco de dados");
                return pessoas; // retorna lista vazia
            }
            while (rs.next()) {
                pessoas.add(new Pessoa(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("telefone"),
                        rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar pessoas: " + e.getMessage());
            e.printStackTrace();
        }
        return pessoas;
    }

    public static boolean deletarPessoa(int id) {
        String sql = "DELETE FROM pessoas WHERE id = ?";
        try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Erro: Falha na conexão com o banco de dados");
                return false;
            }
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar pessoa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
