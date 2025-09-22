import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AgendaApp extends Application {

    private ListView<Pessoa> listView;

    @Override
    public void start(Stage primaryStage) {
        Database.init();

        // Título principal
        Label titleLabel = new Label("📱 Agenda Fácil");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);
        
        Label subtitleLabel = new Label("Gerencie seus contatos de forma simples");
        subtitleLabel.setFont(Font.font("Arial", 14));
        subtitleLabel.setTextFill(Color.GRAY);

        // Lista de contatos
        listView = new ListView<>();
        listView.setPrefHeight(200);
        listView.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        // Set placeholder for empty list
        Label placeholder = new Label("📝 Nenhum contato cadastrado. Adicione o primeiro contato!");
        placeholder.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
        listView.setPlaceholder(placeholder);
        
        // Set cell factory for proper display
        listView.setCellFactory(param -> new ListCell<Pessoa>() {
            @Override
            protected void updateItem(Pessoa pessoa, boolean empty) {
                super.updateItem(pessoa, empty);
                if (empty || pessoa == null) {
                    setText(null);
                } else {
                    String telefone = pessoa.getTelefone() != null && !pessoa.getTelefone().isEmpty() ? " | " + pessoa.getTelefone() : "";
                    String email = pessoa.getEmail() != null && !pessoa.getEmail().isEmpty() ? " | " + pessoa.getEmail() : "";
                    setText(pessoa.getId() + ": " + pessoa.getNome() + telefone + email);
                }
            }
        });
        
        atualizarLista();

        // Campos de entrada
        Label formLabel = new Label("Adicionar Novo Contato");
        formLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        formLabel.setTextFill(Color.DARKGREEN);

        TextField nomeField = new TextField();
        nomeField.setPromptText("Digite o nome completo");
        nomeField.setPrefHeight(35);
        nomeField.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-padding: 8;");

        TextField telField = new TextField();
        telField.setPromptText("Digite o telefone (ex: (11) 99999-9999)");
        telField.setPrefHeight(35);
        telField.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-padding: 8;");

        TextField emailField = new TextField();
        emailField.setPromptText("Digite o email (ex: nome@email.com)");
        emailField.setPrefHeight(35);
        emailField.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-padding: 8;");

        // Botões com estilo
        Button addBtn = new Button("✅ Adicionar Contato");
        addBtn.setPrefHeight(40);
        addBtn.setPrefWidth(200);
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;");
        addBtn.setOnMouseEntered(e -> addBtn.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;"));
        addBtn.setOnMouseExited(e -> addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;"));
        
        addBtn.setOnAction(e -> {
            String nome = nomeField.getText().trim();
            String telefone = telField.getText().trim();
            String email = emailField.getText().trim();
            
            if (nome.isEmpty()) {
                mostrarAlerta("Erro", "O nome é obrigatório!", Alert.AlertType.WARNING);
                return;
            }
            
            Pessoa p = new Pessoa(0, nome, telefone, email);
            boolean sucesso = PessoaDAO.inserirPessoa(p);
            if (sucesso) {
                atualizarLista();
                nomeField.clear(); telField.clear(); emailField.clear();
                mostrarAlerta("Sucesso", "Contato adicionado com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Erro", "Falha ao adicionar contato. Verifique a conexão com o banco de dados.", Alert.AlertType.ERROR);
            }
        });

        Button delBtn = new Button("🗑️ Excluir Selecionado");
        delBtn.setPrefHeight(40);
        delBtn.setPrefWidth(200);
        delBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;");
        delBtn.setOnMouseEntered(e -> delBtn.setStyle("-fx-background-color: #da190b; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;"));
        delBtn.setOnMouseExited(e -> delBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5;"));
        
        delBtn.setOnAction(e -> {
            Pessoa selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirmar Exclusão");
                confirmAlert.setHeaderText("Excluir Contato");
                confirmAlert.setContentText("Tem certeza que deseja excluir este contato?");
                
                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    boolean sucesso = PessoaDAO.deletarPessoa(selected.getId());
                    if (sucesso) {
                        atualizarLista();
                        mostrarAlerta("Sucesso", "Contato excluído com sucesso!", Alert.AlertType.INFORMATION);
                    } else {
                        mostrarAlerta("Erro", "Falha ao excluir contato. Verifique se o contato ainda existe.", Alert.AlertType.ERROR);
                    }
                }
            } else {
                mostrarAlerta("Aviso", "Selecione um contato para excluir!", Alert.AlertType.WARNING);
            }
        });

        // Layout dos botões
        HBox buttonBox = new HBox(10, addBtn, delBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // Layout dos campos
        VBox formBox = new VBox(10);
        formBox.getChildren().addAll(formLabel, nomeField, telField, emailField, buttonBox);
        formBox.setPadding(new Insets(15));
        formBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-radius: 8;");

        // Lista com título
        Label listLabel = new Label("Lista de Contatos");
        listLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        listLabel.setTextFill(Color.DARKBLUE);

        VBox listBox = new VBox(10, listLabel, listView);
        listBox.setPadding(new Insets(15));

        // Layout principal
        VBox headerBox = new VBox(5, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20, 15, 15, 15));
        headerBox.setStyle("-fx-background-color: linear-gradient(to bottom, #e3f2fd, #ffffff);");

        VBox root = new VBox(15);
        root.getChildren().addAll(headerBox, formBox, listBox);
        root.setPadding(new Insets(0, 15, 15, 15));
        root.setStyle("-fx-background-color: #ffffff;");

        Scene scene = new Scene(root, 500, 650);
        primaryStage.setTitle("Agenda Fácil - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void atualizarLista() {
        listView.getItems().clear();
        listView.getItems().addAll(PessoaDAO.listarPessoas());
    }
    
    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
