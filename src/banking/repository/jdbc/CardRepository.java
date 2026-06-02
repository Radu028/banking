package banking.repository.jdbc;

import banking.config.DatabaseConnection;
import banking.model.Card;
import banking.model.CreditCard;
import banking.model.DebitCard;
import banking.repository.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CardRepository implements Repository<Card, String> {
    private final DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

    @Override
    public void save(Card card) throws SQLException {
        String sql = "INSERT INTO cards(card_number, account_iban, holder_name, active, card_type, contactless, credit_limit) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            fillStatement(preparedStatement, card);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Card findById(String cardNumber) throws SQLException {
        String sql = "SELECT card_number, account_iban, holder_name, active, card_type, contactless, credit_limit "
                + "FROM cards WHERE card_number = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cardNumber);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Card> findAll() throws SQLException {
        String sql = "SELECT card_number, account_iban, holder_name, active, card_type, contactless, credit_limit "
                + "FROM cards ORDER BY card_number";
        List<Card> cards = new ArrayList<Card>();

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                cards.add(mapRow(resultSet));
            }
        }

        return cards;
    }

    @Override
    public void update(Card card) throws SQLException {
        String sql = "UPDATE cards SET account_iban = ?, holder_name = ?, active = ?, card_type = ?, "
                + "contactless = ?, credit_limit = ? WHERE card_number = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, card.getAccountIban());
            preparedStatement.setString(2, card.getHolderName());
            preparedStatement.setInt(3, card.isActive() ? 1 : 0);
            preparedStatement.setString(4, card.getCardType());

            if (card instanceof DebitCard) {
                preparedStatement.setInt(5, ((DebitCard) card).isContactless() ? 1 : 0);
            } else {
                preparedStatement.setNull(5, Types.INTEGER);
            }

            if (card instanceof CreditCard) {
                preparedStatement.setDouble(6, ((CreditCard) card).getCreditLimit());
            } else {
                preparedStatement.setNull(6, Types.REAL);
            }

            preparedStatement.setString(7, card.getCardNumber());
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void delete(String cardNumber) throws SQLException {
        String sql = "DELETE FROM cards WHERE card_number = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, cardNumber);
            preparedStatement.executeUpdate();
        }
    }

    private void fillStatement(PreparedStatement preparedStatement, Card card) throws SQLException {
        preparedStatement.setString(1, card.getCardNumber());
        preparedStatement.setString(2, card.getAccountIban());
        preparedStatement.setString(3, card.getHolderName());
        preparedStatement.setInt(4, card.isActive() ? 1 : 0);
        preparedStatement.setString(5, card.getCardType());

        if (card instanceof DebitCard) {
            preparedStatement.setInt(6, ((DebitCard) card).isContactless() ? 1 : 0);
        } else {
            preparedStatement.setNull(6, Types.INTEGER);
        }

        if (card instanceof CreditCard) {
            preparedStatement.setDouble(7, ((CreditCard) card).getCreditLimit());
        } else {
            preparedStatement.setNull(7, Types.REAL);
        }
    }

    private Card mapRow(ResultSet resultSet) throws SQLException {
        String cardType = resultSet.getString("card_type");

        if ("CreditCard".equals(cardType)) {
            CreditCard card = new CreditCard(
                    resultSet.getString("card_number"),
                    resultSet.getString("account_iban"),
                    resultSet.getString("holder_name"),
                    resultSet.getDouble("credit_limit")
            );
            card.setActive(resultSet.getInt("active") == 1);
            return card;
        }

        DebitCard card = new DebitCard(
                resultSet.getString("card_number"),
                resultSet.getString("account_iban"),
                resultSet.getString("holder_name"),
                resultSet.getInt("contactless") == 1
        );
        card.setActive(resultSet.getInt("active") == 1);
        return card;
    }
}
