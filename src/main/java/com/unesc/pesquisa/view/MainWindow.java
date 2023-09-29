package com.unesc.pesquisa.view;

import com.unesc.pesquisa.model.OnSearch;
import com.unesc.pesquisa.model.SearchResult;
import com.unesc.pesquisa.model.SearchType;
import com.unesc.pesquisa.util.TestUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;

public class MainWindow extends JFrame {
    private static final String DEFAULT_FOLDER = "C:\\Users\\mathe\\projetos\\pesquisa-arquivos\\src\\main\\resources\\dataset";
    private JPanel panel;
    private JTextField folderField;
    private JButton findButton;
    private JButton searchButton;
    private JTextField searchValueField;
    private JProgressBar progressBar;
    private JButton testsButton;
    private JTable table;
    private JComboBox<SearchType> typeCombobox;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Nome", "Arquivo", "Tempo médio (ns)", "Linha"}, 0
    );
    private final OnSearch onSearch;

    public MainWindow(OnSearch onSearch) {
        super("Pesquisa em arquivos");
        this.onSearch = onSearch;
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        folderField.setEditable(false);
        folderField.setText(DEFAULT_FOLDER);
        searchButton.addActionListener(this::onSearch);
        findButton.addActionListener(this::selectFolder);
        testsButton.addActionListener(this::executeTests);
        table.setVisible(false);
        table.setModel(tableModel);

        setContentPane(panel);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }

    private void onSearch(ActionEvent event) {
        String folder = folderField.getText();
        String value = searchValueField.getText();

        if (validate(folder, value)) return;

        progressBar.setVisible(true);

        new Thread(() -> {
            SearchResult result = onSearch.search(folder, value, (SearchType) typeCombobox.getSelectedItem());
            progressBar.setVisible(false);
            JOptionPane.showMessageDialog(this, !result.isFound() ? "Pesquisa não encontrada!" : result);
        }).start();
    }

    private boolean validate(String folder, String value) {
        if (folder.isBlank()) {
            JOptionPane.showMessageDialog(this, "Pasta não informada!");
            return true;
        }

        if (value.isBlank()) {
            JOptionPane.showMessageDialog(this, "Termo de pesquisa não informado!");
            return true;
        }
        return false;
    }

    private void selectFolder(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Selecionar pasta");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            folderField.setText(chooser.getSelectedFile().toString());
        }
    }

    private void executeTests(ActionEvent event) {
        String input = JOptionPane.showInputDialog(this, "Número de rodadas de pesquisa por nome");

        if (input == null || input.isBlank()) {
            return;
        }

        try {
            runTests(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número inválido");
        }
    }

    private void runTests(int numberOfTests) {
        String folder = folderField.getText();
        SearchType searchType = (SearchType) typeCombobox.getSelectedItem();
        String logFileName = searchType.name() + ".csv";

        if (folder.isBlank()) {
            JOptionPane.showMessageDialog(this, "Pasta não informada!");
            return;
        }
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);

        File file = new File(logFileName);

        if (file.exists()) {
            file.delete();
        }

        tableModel.setRowCount(0);
        table.setVisible(true);

        new Thread(() -> {
            NumberFormat numberFormat = NumberFormat.getInstance();

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                fileOutputStream.write("nome;arquivo;tempo;linha".getBytes(StandardCharsets.UTF_8));
                progressBar.setIndeterminate(false);
                progressBar.setMinimum(0);
                progressBar.setMaximum(numberOfTests * TestUtils.TEST_NAMES.length);

                long somaTotal = 0;

                for (int i = 0; i < TestUtils.TEST_NAMES.length; i++) {
                    String name = TestUtils.TEST_NAMES[i];

                    SearchResult result = null;
                    long soma = 0;

                    for (int j = 0; j < numberOfTests; j++) {
                        result = onSearch.search(folder, name, searchType);
                        soma += result.getSearchTime();
                        String csv = "\n" +
                                result.getTerm() + ";" +
                                result.getFile() + ";" +
                                numberFormat.format(result.getSearchTime()) + ";" +
                                result.getRow();
                        fileOutputStream.write(csv.getBytes(StandardCharsets.UTF_8));
                        fileOutputStream.flush();
                        progressBar.setValue(i * j);
                    }

                    long media = soma / numberOfTests;

                    tableModel.addRow(new Object[]{
                            result.getTerm(),
                            result.getFile(),
                            numberFormat.format(media),
                            result.getRow()
                    });

                    somaTotal += media;
                }

                tableModel.addRow(new Object[]{
                        "Média total", "", numberFormat.format(somaTotal / TestUtils.TEST_NAMES.length), ""
                });
            } catch (Exception e) {
                progressBar.setVisible(false);
                throw new RuntimeException(e);
            }

            progressBar.setVisible(false);
        }).start();
    }

    private void createUIComponents() {
        typeCombobox = new JComboBox<>(SearchType.values());
    }
}
