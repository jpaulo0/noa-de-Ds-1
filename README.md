package view;

import java.util.List;
import java.util.Scanner;
import controller.ContatoController;
import model.Pessoa;

public class ContatoView {
    private ContatoController controller;
    private Scanner scanner;

    public ContatoView() {
        this.controller = new ContatoController();
        this.scanner = new Scanner(System.in);
    }

    public void exibirMenu() {
        int opcao;

        do {
            System.out.println("\n========== AGENDA FÁCIL ==========");
            System.out.println("1. Cadastrar contato");
            System.out.println("2. Listar todos os contatos");
            System.out.println("3. Atualizar contato");
            System.out.println("4. Excluir contato");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        cadastrarContato();
                        break;
                    case 2:
                        listarContatos();
                        break;
                    case 3:
                        atualizarContato();
                        break;
                    case 4:
                        excluirContato();
                        break;
                    case 5:
                        System.out.println("Obrigado por usar a Agenda Fácil!");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                opcao = 0;
                System.out.println("Por favor, digite um número válido!");
            }

        } while (opcao != 5);

        scanner.close();
    }

    private void cadastrarContato() {
        System.out.println("\n--- CADASTRAR CONTATO ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        controller.cadastrarContato(nome, telefone, email);
    }

    private void listarContatos() {
        System.out.println("\n--- LISTA DE CONTATOS ---");
        List<Pessoa> contatos = controller.listarContatos();

        if (contatos.isEmpty()) {
            System.out.println("Nenhum contato cadastrado.");
        } else {
            for (Pessoa contato : contatos) {
                System.out.println(contato);
            }
        }
    }

    private void atualizarContato() {
        System.out.println("\n--- ATUALIZAR CONTATO ---");
        System.out.print("Digite o ID do contato: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Novo nome: ");
            String nome = scanner.nextLine();
            System.out.print("Novo telefone: ");
            String telefone = scanner.nextLine();
            System.out.print("Novo email: ");
            String email = scanner.nextLine();

            controller.atualizarContato(id, nome, telefone, email);
        } catch (NumberFormatException e) {
            System.out.println("ID deve ser um número!");
        }
    }

    private void excluirContato() {
        System.out.println("\n--- EXCLUIR CONTATO ---");
        System.out.print("Digite o ID do contato: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            controller.excluirContato(id);
        } catch (NumberFormatException e) {
            System.out.println("ID deve ser um número!");
        }
    }
}
###Db
package db;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:postgresql://ep-spring-recipe-adeacguf-pooler.c-2.us-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_SvDt3Kigjm4J&sslmode=require";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void criarTabela() {
        String sql = """
            CREATE TABLE IF NOT EXISTS contatos (
                id SERIAL PRIMARY KEY,
                nome TEXT NOT NULL,
                telefone TEXT NOT NULL,
                email TEXT NOT NULL
            )
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela 'contatos' criada ou já existe.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabela: " + e.getMessage());
        }
    }
}
