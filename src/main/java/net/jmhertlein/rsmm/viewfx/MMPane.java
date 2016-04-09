package net.jmhertlein.rsmm.viewfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import net.jmhertlein.rsmm.model.*;
import net.jmhertlein.rsmm.viewfx.util.Dialogs;
import net.jmhertlein.rsmm.viewfx.util.FXMLSplitPane;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Created by joshua on 3/11/16.
 */
public class MMPane extends FXMLSplitPane {
    private final Connection conn;
    private final ItemManager items;
    private final QuoteManager quotes;
    private final TurnManager turns;
    private final TradeManager trades;

    private final ObservableList<Item> itemList;
    private final ObservableList<Trade> shownTrades;
    private final ObservableList<Turn> openTurnsList;
    private final ObservableList<Quote> shownQuotes;
    private Optional<Integer> geLimitForShownQuoteItem;

    @FXML
    private TableView<Trade> tradeTable;
    @FXML
    private TableColumn<Trade, LocalDateTime> tradeTimeColumn;
    @FXML
    private TableColumn<Trade, Integer> tradeQuantityColumns;
    @FXML
    private TableColumn<Trade, Integer> tradePriceColumn;
    @FXML
    private TableView<Quote> quoteTable;
    @FXML
    private TableColumn<Quote, Timestamp> quoteDateColumn;
    @FXML
    private TableColumn<Quote, Integer> quoteBidColumn;
    @FXML
    private TableColumn<Quote, Integer> quoteAskColumn;
    @FXML
    private TableColumn<Quote, RSInteger> quoteSpreadColumn;
    @FXML
    private TableColumn<Quote, RSInteger> quotePPLColumn;
    @FXML
    private ComboBox<Item> quoteItemChooser;
    @FXML
    private TextField quoteBidField;
    @FXML
    private TextField quoteAskField;
    @FXML
    private TableView<Turn> turnTable;
    @FXML
    private TableColumn<Turn, String> turnItemColumn;
    @FXML
    private TableColumn<Turn, Integer> turnQuantityColumn;
    @FXML
    private TableColumn<Turn, Integer> turnPositionCostColumn;
    @FXML
    private TableColumn<Turn, Integer> turnUsedLimitColumn;
    @FXML
    private TableColumn<Turn, Integer> turnMaxLimitColumn;
    @FXML
    private TableColumn<Turn, RSInteger> turnOpenProfitColumn;
    @FXML
    private TableColumn<Turn, RSInteger> turnClosedProfitColumn;
    @FXML
    private Button closeTurnButton;


    public MMPane(Connection conn) {
        super("/fxml/mmpane.fxml");
        this.conn = conn;

        try {
            items = new ItemManager(conn);
            quotes = new QuoteManager(conn);
            turns = new TurnManager(conn, items, quotes);
        } catch (SQLException | NoSuchItemException | NoQuoteException e) {
            Dialogs.showMessage("Load Error", "Error reading from database.", e);
            return;
        }

        itemList = FXCollections.observableArrayList();
        shownTrades = FXCollections.observableArrayList();
        shownQuotes = FXCollections.observableArrayList();
        openTurnsList = FXCollections.observableArrayList();

        try {
            itemList.addAll(items.getItems());
            openTurnsList.addAll(turns.getOpenTurns());
        } catch (SQLException e) {
            Dialogs.showMessage("Error Loading", "Error populating initial data.", e);
            return;
        }


        turnTable.setItems(openTurnsList);
        tradeTable.setItems(shownTrades);
        quoteTable.setItems(shownQuotes);
        quoteItemChooser.setItems(itemList);

        map(quoteDateColumn, "quoteTS");
        map(quoteAskColumn, "ask");
        map(quoteBidColumn, "bid");
    }

    private static <S, T> void map(TableColumn<S, T> col, String field) {
        col.setCellValueFactory(new PropertyValueFactory<>(field));
    }

    @FXML
    void addBuyTrade(ActionEvent event) {
        System.out.println("CLICK");
    }

    @FXML
    void addQuote(ActionEvent event) {
        RSInteger bid, ask;
        try {
            bid = RSInteger.parseInt(quoteBidField.getText());
            ask = RSInteger.parseInt(quoteAskField.getText());
        } catch (NumberFormatException ex) {
            Dialogs.showMessage("Invalid Price", "Invalid Price", ex);
            return;
        }

        if (!(bid.intValue() < ask.intValue())) {
            Dialogs.showMessage("Invalid Price", "Invalid Price", "Bid must be < ask");
            return;
        }

        Item selected = quoteItemChooser.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.showMessage("Invalid Item", "Invalid Item", "No item selected.");
            return;
        }

        try {
            quotes.addQuote(selected.getName(), bid.intValue(), ask.intValue());
            quotes.fireUpdateEvent();
            quoteBidField.setText("");
            quoteAskField.setText("");
        } catch (SQLException ex) {
            Dialogs.showMessage("Error Adding Quote", "Error Adding Quote", ex);
        }
    }

    @FXML
    void addSellTrade(ActionEvent event) {
        System.out.println("CLICK");
    }

    @FXML
    void closeTurn(ActionEvent event) {
        System.out.println("CLICK");
    }

    @FXML
    void openTurn(ActionEvent event) {
        System.out.println("CLICK");
    }

    @FXML
    void showQuotesForItem(ActionEvent event) {
        Optional.ofNullable(quoteItemChooser.getSelectionModel().getSelectedItem()).ifPresent(item -> {
            try {
                shownQuotes.setAll(quotes.getLatestQuotes(item.getName()));
            } catch (SQLException e) {
                Dialogs.showMessage("Error Showing Quotes", "Error showing quotes for " + item.getName(), e);
            }
        });
    }

    @FXML
    void syncQuoteToTurn(ActionEvent event) {
        Optional.ofNullable(turnTable.getSelectionModel().getSelectedItem()).ifPresent(turn -> {
            quoteItemChooser.getSelectionModel().clearSelection();
            quoteItemChooser.getSelectionModel().select(itemList.stream().filter(i -> i.getName().equals(turn.getItemName())).findFirst().get());
            try {
                shownQuotes.setAll(quotes.getLatestQuotes(turn.getItemName()));
            } catch (SQLException e) {
                Dialogs.showMessage("Error Showing Quotes", "Error showing quotes for " + turn.getItemName(), e);
            }
        });
    }

}
