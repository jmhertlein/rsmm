package cafe.josh.rsmm.viewfx;

import cafe.josh.joshfx.Dialogs;
import cafe.josh.joshfx.FXMLBorderPane;
import cafe.josh.rsmm.controller.*;
import cafe.josh.rsmm.model.*;
import cafe.josh.rsmm.viewfx.util.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import cafe.josh.rsmm.controller.util.Side;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MMPane extends FXMLBorderPane {
    private final Connection conn;
    private final ItemManager items;
    private final QuoteManager quotes;
    private final TurnManager turns;

    private final ObservableList<Quote> quoteTableQuotes;
    private final ObservableList<Item> quoteItemChooserItems;
    private final ObservableList<Turn> turnTableTurns;
    private final ObservableList<Trade> tradeTableTrades;

    private final ObservableList<ItemLimitUsageState> limitUsageStates;

    private final IntegerProperty gePriceProperty;

    @FXML
    private TableView<Trade> tradeTable;
    @FXML
    private TableColumn<Trade, Timestamp> tradeTimeColumn;
    @FXML
    private TableColumn<Trade, Integer> tradeQuantityColumns;
    @FXML
    private TableColumn<Trade, Integer> tradePriceColumn;
    @FXML
    private TextField tradeQuantityField;
    @FXML
    private Label gePriceLabel;
    @FXML
    private TableView<Quote> quoteTable;
    @FXML
    private TableColumn<Quote, Timestamp> quoteDateColumn;
    @FXML
    private TableColumn<Quote, Integer> quoteBidColumn;
    @FXML
    private TableColumn<Quote, Integer> quoteAskColumn;
    @FXML
    private TableColumn<Quote, Integer> quoteSpreadColumn;
    @FXML
    private TableColumn<Quote, Integer> quotePPLColumn;
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
    private TableColumn<Turn, Integer> turnOpenProfitColumn;
    @FXML
    private TableColumn<Turn, Integer> turnClosedProfitColumn;
    @FXML
    private Button closeTurnButton;
    @FXML
    private TableView<ItemLimitUsageState> limitTable;
    @FXML
    private TableColumn<ItemLimitUsageState, Item> limitItemColumn;
    @FXML
    private TableColumn<ItemLimitUsageState, Integer> limitRemainingColumn;
    @FXML
    private Label totalProfitLabel;
    @FXML
    private Label todayProfitLabel;
    @FXML
    private Label pendingProfitLabel;
    @FXML
    private Label openProfitLabel;
    @FXML
    private Label sumPositionCostLabel;
    @FXML
    private Label quoteCostLabel;
    @FXML
    private CheckBox sellModeCheckBox;
    @FXML
    private CheckMenuItem alwaysOnTopCheckBox;


    public MMPane(Connection conn) {
        super("/fxml/mmpane.fxml");
        this.conn = conn;

        try {
            items = new ItemManager(conn);
            quotes = new QuoteManager(conn);
            turns = new TurnManager(conn, items, quotes);
        } catch (SQLException | NoSuchItemException | NoQuoteException e) {
            Dialogs.showMessage("Load Error", "Error reading from database.", e);
            throw new RuntimeException("Couldn't load startup data.");
        }

        GlobalStatsManager statsManager = new GlobalStatsManager(conn, turns, quotes);

        try {
            statsManager.recalculateProfit();
            statsManager.recalculateDailyQuoteCost();
        } catch (SQLException | NoSuchItemException | NoQuoteException e) {
            Dialogs.showMessage("Load Error", "Error reading from database.", e);
            throw new RuntimeException("Couldn't load startup data.");
        }

        quoteTable.setRowFactory(new QuoteTableRowFactory());
        turnOpenProfitColumn.setCellFactory(TextFieldTableCell.forTableColumn(new RSIntegerStringConverter()));
        turnClosedProfitColumn.setCellFactory(TextFieldTableCell.forTableColumn(new RSIntegerStringConverter()));
        turnPositionCostColumn.setCellFactory(TextFieldTableCell.forTableColumn(new RSIntegerStringConverter()));
        quotePPLColumn.setCellFactory(TextFieldTableCell.forTableColumn(new RSIntegerStringConverter()));
        quoteDateColumn.setCellFactory(TextFieldTableCell.forTableColumn(new TimestampStringConverter()));
        tradeTimeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new TimestampStringConverter()));

        totalProfitLabel.textProperty().bindBidirectional(statsManager.totalClosedProfitProperty(), new RSNumberStringConverter());
        todayProfitLabel.textProperty().bindBidirectional(statsManager.profitTodayProperty(), new RSNumberStringConverter());
        pendingProfitLabel.textProperty().bindBidirectional(statsManager.pendingProfitProperty(), new RSNumberStringConverter());
        openProfitLabel.textProperty().bindBidirectional(statsManager.openProfitProperty(), new RSNumberStringConverter());
        sumPositionCostLabel.textProperty().bindBidirectional(statsManager.sumPositionCostProperty(), new RSNumberStringConverter());
        quoteCostLabel.textProperty().bindBidirectional(statsManager.dailyQuoteCostProperty(), new RSNumberStringConverter());


        quoteItemChooserItems = FXCollections.observableArrayList();
        turnTableTurns = FXCollections.observableArrayList();
        tradeTableTrades = FXCollections.observableArrayList();
        quoteTableQuotes = FXCollections.observableArrayList();
        limitUsageStates = FXCollections.observableArrayList();

        gePriceProperty = new SimpleIntegerProperty();
        gePriceLabel.textProperty().bindBidirectional(gePriceProperty, new NumberStringConverter());

        turnTableTurns.setAll(turns.getOpenTurns());
        quoteItemChooserItems.setAll(items.getItems().stream().filter(Item::isFavorite).collect(Collectors.toList()));

        turnTable.setItems(turnTableTurns);
        quoteItemChooser.setItems(quoteItemChooserItems);
        tradeTable.setItems(tradeTableTrades);
        quoteTable.setItems(quoteTableQuotes);
        limitTable.setItems(limitUsageStates);

        map(quoteDateColumn, "quoteTS");
        map(quoteAskColumn, "ask");
        map(quoteBidColumn, "bid");
        map(quoteSpreadColumn, "spread");
        map(quotePPLColumn, "profitPerLimit");

        map(turnItemColumn, "item");
        map(turnQuantityColumn, "position");
        map(turnPositionCostColumn, "positionCost");
        map(turnOpenProfitColumn, "openProfit");
        map(turnClosedProfitColumn, "closedProfit");

        map(tradeTimeColumn, "tradeTime");
        map(tradeQuantityColumns, "quantity");
        map(tradePriceColumn, "price");

        map(limitItemColumn, "item");
        map(limitRemainingColumn, "limitLeft");

        turnTable.getSelectionModel().selectedItemProperty().addListener((obs, old, nu) -> {
            if (nu != null) {
                tradeTableTrades.setAll(nu.getTrades());
            } else {
                tradeTableTrades.clear();
            }
        });

        turns.addTradeListener(new RecalculateProfitOnTradeListener(quotes));
        quotes.addListener(new QuoteTableQuoteListener(quoteTableQuotes));
        quotes.addListener(new RecalculateTurnStatsOnQuoteListener(turns, quotes));

        turns.addTradeListener(new TradeTableTradeListener(tradeTableTrades, turnTable.getSelectionModel().selectedItemProperty()));
        turns.addTurnListener(new TurnTableTurnListener(turnTableTurns));

        quotes.addListener(statsManager);
        turns.addTurnListener(statsManager);
        turns.addTradeListener(statsManager);

        quotes.addListener(q -> refreshLimitUsages());
        turns.addTradeListener(new RefreshLimitUsagesTradeListener(this::refreshLimitUsages));

        items.addListener(i -> {
            if (i.isFavorite()) {
                quoteItemChooserItems.add(i);
            } else {
                quoteItemChooserItems.remove(i);
            }
        });

        refreshLimitUsages();
    }

    private static <S, T> void map(TableColumn<S, T> col, String field) {
        col.setCellValueFactory(new PropertyValueFactory<>(field));
    }

    @FXML
    void addBuyTrade() {
        addTrade(Side.BID);
    }

    @FXML
    void refreshLimitUsages() {
        Map<Item, Integer> usage = new HashMap<>();
        try {
            try (PreparedStatement ps = conn.prepareStatement("select item_id, count(quote_ts) from quote where quote_ts > now() - interval '4 hours' and not synthetic group by item_id;");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Optional<Item> i = items.getItem(rs.getInt("item_id"));
                    if (!i.isPresent()) {
                        throw new NoSuchItemException(rs.getInt("item_id"));
                    }

                    usage.put(i.get(), usage.getOrDefault(i.get(), 0) + rs.getInt("count"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement("select item_id, sum(quantity) from trade natural join turn where trade_ts > now() - interval '4 hours' and quantity > 0 group by item_id;");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Optional<Item> i = items.getItem(rs.getInt("item_id"));
                    if (!i.isPresent()) {
                        throw new NoSuchItemException(rs.getInt("item_id"));
                    }

                    usage.put(i.get(), usage.getOrDefault(i.get(), 0) + rs.getInt("sum"));
                }
            }
        } catch (SQLException | NoSuchItemException e) {
            Dialogs.showMessage("Error Retrieving Limit Usages", "Error Retrieving Limit Usages", e);
            limitUsageStates.clear();
            return;
        }

        limitUsageStates.clear();
        for (Map.Entry<Item, Integer> e : usage.entrySet()) {
            limitUsageStates.add(new ItemLimitUsageState(e.getKey(), e.getKey().getBuyLimit() - e.getValue()));
        }
    }

    private void addTrade(Side side) {
        Optional.ofNullable(turnTable.getSelectionModel().getSelectedItem()).ifPresent(turn -> {
            int qty;
            try {
                qty = Integer.parseInt(tradeQuantityField.getText());
            } catch (NumberFormatException e) {
                Dialogs.showMessage("Invalid Trade Quantity", "Invalid Trade Quantity", e);
                return;
            }

            Optional<Quote> quote = Optional.ofNullable(quoteTable.getSelectionModel().getSelectedItem());
            if (!quote.isPresent()) {
                Dialogs.showMessage("No Quote Selected", "No Quote Selected", "You need to select a quote.");
                return;
            }

            if (!quote.get().getItem().equals(turn.getItem())) {
                Dialogs.showMessage("Quote Item Mismatch", "Quote Doesn't Match Turn", "The selected quote is not for the same item as the current turn.");
                return;
            }

            final int realQty, px;
            if (side == Side.BID) {
                realQty = qty;
                px = quote.get().getBid();
            } else if (sellModeCheckBox.isSelected()) {
                int ask = quote.get().getAsk();
                if (qty % ask != 0) {
                    Dialogs.showMessage("Invalid Sum", "Invalid Sum", "Your gold amount (" + qty + ") divided by the ask (" + ask + ") does not divide evenly.");
                    return;
                }
                realQty = qty / ask;
                px = ask;
            } else {
                realQty = qty;
                px = quote.get().getAsk();
            }

            try {
                Trade trade = turn.addTrade(px, realQty * side.getMultiplier());
                tradeQuantityField.clear();
                tradeTable.getSelectionModel().select(trade);
                tradeTable.scrollTo(trade);
            } catch (SQLException e) {
                Dialogs.showMessage("Error Adding Trade", "Error Adding Trade", e);
            }
        });
    }

    @FXML
    void addQuote() {
        int bid, ask;
        try {
            bid = Integer.parseInt(quoteBidField.getText());
            ask = Integer.parseInt(quoteAskField.getText());
        } catch (NumberFormatException ex) {
            Dialogs.showMessage("Invalid Price", "Invalid Price", ex);
            return;
        }

        if (!(bid < ask)) {
            Dialogs.showMessage("Invalid Price", "Invalid Price", "Bid must be < ask");
            return;
        }

        Item selected = quoteItemChooser.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Dialogs.showMessage("Invalid Item", "Invalid Item", "No item selected.");
            return;
        }

        try {
            Quote quote = quotes.addQuote(selected, bid, ask);
            quoteBidField.setText("");
            quoteAskField.setText("");
            quoteTable.getSelectionModel().select(quote);
            quoteTable.scrollTo(quote);
        } catch (SQLException ex) {
            Dialogs.showMessage("Error Adding Quote", "Error Adding Quote", ex);
        }
    }

    @FXML
    void addSellTrade() {
        addTrade(Side.ASK);
    }

    @FXML
    void closeTurn() {
        Optional.ofNullable(turnTable.getSelectionModel().getSelectedItem()).ifPresent((turn) -> {
            try {
                if (turn.isFlat()) {
                    turns.closeTurn(turn.getTurnId());
                    if (turn.getClosedProfit().compareTo(BigDecimal.valueOf(1000000)) > 0) {
                        try {
                            Desktop.getDesktop().browse(URI.create("https://www.youtube.com/watch?v=A1a5QQoHh60"));
                        } catch (Throwable e) {
                            try {
                                Runtime.getRuntime().exec("chromium https://www.youtube.com/watch?v=A1a5QQoHh60");
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                } else {
                    Dialogs.showMessage("Error Closing Turn", "Turn Is Not Flat", "Turns must be flat to be able to close them.");
                }
            } catch (SQLException e) {
                Dialogs.showMessage("Error Closing Turn", "SQLException", e);
            }
        });
    }

    @FXML
    void openTurn() {
        Optional<Item> item = Optional.ofNullable(quoteItemChooser.getSelectionModel().getSelectedItem());
        item.ifPresent((i) -> {
            try {
                turns.newTurn(i, quotes);
            } catch (SQLException | DuplicateOpenTurnException | NoQuoteException | NoSuchItemException e) {
                Dialogs.showMessage("Error Opening Turn", "Error Opening Turn", e);
            }
        });
    }

    @FXML
    void showQuotesForItem() {
        Optional.ofNullable(quoteItemChooser.getSelectionModel().getSelectedItem()).ifPresent(item -> {
            try {
                quoteTableQuotes.setAll(quotes.getQuotesFor(item, LocalDate.now()));
                try (PreparedStatement ps = conn.prepareStatement("SELECT price FROM Price WHERE item_id=? ORDER BY day DESC LIMIT 1;")) {
                    ps.setInt(1, item.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            gePriceProperty.set(rs.getInt("price"));
                        } else {
                            gePriceProperty.set(-1);
                        }
                    }
                }
            } catch (SQLException e) {
                Dialogs.showMessage("Error Showing Quotes", "Error showing quotes for " + item.getName(), e);
            }
        });
    }

    @FXML
    void syncQuoteToTurn() {
        Optional.ofNullable(turnTable.getSelectionModel().getSelectedItem()).ifPresent(turn -> {
            quoteItemChooser.getSelectionModel().clearSelection();
            Optional<Item> item = items.getItem(turn.getItemName());
            item.ifPresent((i) -> {
                quoteItemChooser.getSelectionModel().select(i);
                try {
                    quoteTableQuotes.setAll(quotes.getQuotesFor(i, LocalDate.now()));
                    quoteTableQuotes.stream().max((a, b) -> a.getQuoteTS().compareTo(b.getQuoteTS()))
                            .ifPresent(q -> {
                                quoteTable.getSelectionModel().select(q);
                                quoteTable.scrollTo(q);
                            });
                } catch (SQLException e) {
                    Dialogs.showMessage("Error Showing Quotes", "Error showing quotes for " + i.getName(), e);
                }
            });

        });
    }

    @FXML
    void bustTrade() {
        Optional<Trade> trade = Optional.ofNullable(tradeTable.getSelectionModel().getSelectedItem());
        trade.ifPresent(t -> {
            try {
                t.getTurn().bustTrade(t);
            } catch (SQLException e) {
                Dialogs.showMessage("Error Busting Trade", "Error Busting Trade", e);
            }
        });
    }

    @FXML
    void markQuoteSynthetic() {
        Optional<Quote> quote = Optional.ofNullable(quoteTable.getSelectionModel().getSelectedItem());
        quote.ifPresent(q -> {
            try {
                quotes.setQuoteSynthetic(q);
            } catch (SQLException e) {
                Dialogs.showMessage("Error Marking Quote", "Could Not Mark Quote Synthetic", e);
            }
        });
    }

    @FXML
    void showItemWindow() {
        Stage s = new Stage();
        s.setScene(new Scene(new FavoriteItemPane(items)));
        s.setTitle("Favorite Items");
        s.initModality(Modality.APPLICATION_MODAL);
        s.getIcons().add(new Image("/coins.png"));
        s.showAndWait();
    }

    @FXML
    void deleteSelectedQuote() {
        Optional<Quote> quote = Optional.ofNullable(quoteTable.getSelectionModel().getSelectedItem());
        quote.ifPresent(q -> {
            try {
                quotes.deleteQuote(q);
            } catch (SQLException e) {
                Dialogs.showMessage("Error Deleting Quote", "Could Not Delete Quote", e);
            }
        });
    }

    private void beatSelectedQuote(int amt, Side side) {
        Optional<Quote> quote = Optional.ofNullable(quoteTable.getSelectionModel().getSelectedItem());
        quote.ifPresent(q -> {
            try {
                Item item = q.getItem();
                int bid, ask;
                if (side == Side.BID) {
                    bid = q.getBid() + amt;
                } else {
                    bid = q.getBid();
                }

                if (side == Side.ASK) {
                    ask = q.getAsk() - amt;
                } else {
                    ask = q.getAsk();
                }

                if (!(bid < ask)) {
                    Dialogs.showMessage("Spread Error", "Could Not Tighten Spread", "The spread is too tight to perform the requested action.");
                    return;
                }

                Quote newQuote = quotes.addQuote(item, bid, ask);
                quotes.setQuoteSynthetic(newQuote);
                quoteTable.getSelectionModel().select(newQuote);
                quoteTable.scrollTo(newQuote);
            } catch (SQLException e) {
                Dialogs.showMessage("Error Beating Quote", "Could Not Create New Quote", e);
            }
        });
    }


    @FXML
    void beatQuoteBid() {
        beatSelectedQuote(1, Side.BID);
    }

    @FXML
    void beatQuoteAsk() {
        beatSelectedQuote(1, Side.ASK);
    }

    @FXML
    void leapfrogQuoteBid() {
        beatSelectedQuote(2, Side.BID);
    }

    @FXML
    void leapfrogQuoteAsk() {
        beatSelectedQuote(2, Side.ASK);
    }

    @FXML
    void toggleAlwaysOnTop() {
        ((Stage) this.getScene().getWindow()).setAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
    }
}
