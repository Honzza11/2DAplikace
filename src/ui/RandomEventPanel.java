package ui;

import model.Card;
import model.CardLoader;
import model.Player;
import model.Relic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomEventPanel extends JPanel {
    private final GameWindow gameWindow;
    private final Player player;

    private static class Choice {
        final String label;
        final boolean enabled;
        final Runnable action;

        Choice(String label, boolean enabled, Runnable action) {
            this.label = label;
            this.enabled = enabled;
            this.action = action;
        }
    }

    public RandomEventPanel(GameWindow gameWindow, Player player) {
        this.gameWindow = gameWindow;
        this.player = player;

        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 30));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        JLabel titleLabel = new JLabel("Random Event", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 44));
        titleLabel.setForeground(Color.ORANGE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel statsLabel = new JLabel("HP: " + player.getHealth() + "/" + player.getMaxHealth() + "   💰 " + player.getGold() + " Gold");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 26));
        statsLabel.setForeground(new Color(230, 230, 230));
        topPanel.add(statsLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        JLabel subtitleLabel = new JLabel("");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        subtitleLabel.setForeground(new Color(255, 180, 50));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea eventText = new JTextArea();
        eventText.setEditable(false);
        eventText.setOpaque(false);
        eventText.setFont(new Font("Arial", Font.PLAIN, 18));
        eventText.setForeground(new Color(220, 220, 220));
        eventText.setLineWrap(true);
        eventText.setWrapStyleWord(true);
        eventText.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(eventText);
        centerPanel.add(Box.createVerticalStrut(10));

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        add(bottomPanel, BorderLayout.SOUTH);


        Random random = new Random();
        int eventId = random.nextInt(5);
        EventData event = buildEvent(eventId);

        titleLabel.setText(event.title);
        subtitleLabel.setText(event.subtitle);
        eventText.setText(event.text);

        for (Choice choice : event.choices) {
            JButton btn = new JButton(choice.label);
            btn.setFont(new Font("Arial", Font.BOLD, 22));
            btn.setFocusPainted(false);
            btn.setEnabled(choice.enabled);
            btn.setBackground(choice.enabled ? new Color(218, 165, 32) : new Color(90, 90, 90));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

            btn.addActionListener(e -> {
                choice.action.run();
                gameWindow.showScreen("MAP");
            });

            bottomPanel.add(btn);
            bottomPanel.add(Box.createVerticalStrut(12));
        }
    }

    private static class EventData {
        final String title;
        final String subtitle;
        final String text;
        final List<Choice> choices;

        EventData(String title, String subtitle, String text, List<Choice> choices) {
            this.title = title;
            this.subtitle = subtitle;
            this.text = text;
            this.choices = choices;
        }
    }

    private EventData buildEvent(int eventId) {

        switch (eventId) {
            case 0:
                return darkAltarEvent();
            case 1:
                return lostCacheEvent();
            case 2:
                return cursedShrineEvent();
            case 3:
                return anvilDealEvent();
            default:
                return merchantDealEvent();
        }
    }

    private EventData darkAltarEvent() {
        int costHp = 15;
        Relic guaranteedRelic = null;

        List<Choice> choices = new ArrayList<>();
        choices.add(new Choice(
                "Sacrifice " + costHp + " HP (gain a relic)",
                player.getHealth() > costHp,
                () -> {
                    player.takeDamage(costHp);
                    Relic relic = guaranteedRelic != null ? guaranteedRelic : Relic.getRandomRelic();
                    if (relic != null) player.addRelic(relic);
                }
        ));

        int heal = 20;
        choices.add(new Choice(
                "Quiet prayer (+ " + heal + " HP)",
                true,
                () -> player.heal(heal)
        ));

        return new EventData(
                "Dark Altar",
                "An unknown voice tests you...",
                "You stand before a sinister altar. Two paths open to you: sacrifice part of your life, or risk only a brief moment of calm.",
                choices
        );
    }

    private EventData lostCacheEvent() {
        int goldGain = 25 + new Random().nextInt(21);
        List<Choice> choices = new ArrayList<>();

        choices.add(new Choice(
                "Take gold (+ " + goldGain + " )",
                true,
                () -> player.addGold(goldGain)
        ));

        choices.add(new Choice(
                "Find a curious card (add to deck)",
                true,
                () -> {
                    Card c = pickRandomCardForPlayer();
                    if (c != null) player.getDeck().add(new Card(c));
                }
        ));

        return new EventData(
                "Lost Cache",
                "A hand lingered here, craving neither time nor patience...",
                "You find traces of a hidden place. It might help you, or it might simply please you for a moment.",
                choices
        );
    }

    private EventData cursedShrineEvent() {
        int loseHp = 8;
        int relicCount = 1;

        List<Choice> choices = new ArrayList<>();
        choices.add(new Choice(
                "Accept the curse (" + loseHp + " HP) and gain a relic",
                player.getHealth() > loseHp,
                () -> {
                    player.takeDamage(loseHp);
                    for (int i = 0; i < relicCount; i++) {
                        Relic r = Relic.getRandomRelic();
                        if (r != null) player.addRelic(r);
                    }
                }
        ));

        choices.add(new Choice(
                "Flee (nothing happens)",
                true,
                () -> {

                }
        ));

        return new EventData(
                "Cursed Shrine",
                "Silence turns into pressure...",
                "Red dust lies on the ground. When you breathe in, you feel the cost arriving before you can decide.",
                choices
        );
    }

    private EventData anvilDealEvent() {
        int upgradeCostGold = 60;
        List<Choice> choices = new ArrayList<>();

        choices.add(new Choice(
                "Pay " + upgradeCostGold + "  to upgrade a card",
                player.getGold() >= upgradeCostGold,
                () -> {
                    player.removeGold(upgradeCostGold);
                    gameWindow.showSmithDialog(card -> {
                        if (card != null) card.upgrade();
                    });
                }
        ));

        choices.add(new Choice(
                "Gain a small reprieve (+12 HP)",
                true,
                () -> player.heal(12)
        ));

        return new EventData(
                "Blacksmith's Offer",
                "Steel demands its fee.",
                "You hear a hammer strike. The craftsman can forge a better version of a card for you, but he wants to be paid in gold.",
                choices
        );
    }

    private EventData merchantDealEvent() {
        int goldCost = 35;
        int goldGain = 25 + new Random().nextInt(16); // 25..40
        int heal = 10;

        List<Choice> choices = new ArrayList<>();

        choices.add(new Choice(
                "Pay " + goldCost + "  and gain a relic",
                player.getGold() >= goldCost,
                () -> {
                    player.removeGold(goldCost);
                    Relic r = Relic.getRandomRelic();
                    if (r != null) player.addRelic(r);
                }
        ));

        choices.add(new Choice(
                "Trade experience: lose gold, but catch your breath (-" + goldCost + " , +" + heal + " HP)",
                player.getGold() >= goldCost,
                () -> {
                    player.removeGold(goldCost);
                    player.heal(heal);
                }
        ));

        // Small free option if player can't pay.
        if (player.getGold() < goldCost) {
            choices.add(new Choice(
                    "Just take a look (nothing)",
                    true,
                    () -> {}
            ));
        } else {
            choices.add(new Choice(
                    "Base exchange: take gold (+ " + goldGain + " )",
                    true,
                    () -> player.addGold(goldGain)
            ));
        }

        return new EventData(
                "Odd Merchant",
                "Fortune is a commodity that can't be weighed.",
                "A merchant circles you, speaking faster than usual. Two deals are offered, with a third option for whatever conscience remains.",
                choices
        );
    }

    private Card pickRandomCardForPlayer() {
        List<Card> allCards = CardLoader.loadCards("Res/cards.json");
        if (allCards == null || allCards.isEmpty()) return null;

        List<Card> valid = new ArrayList<>();
        String playerClass = player.getClass().getSimpleName();
        for (Card c : allCards) {
            if (c.getHeroClass().equalsIgnoreCase("Neutral") || c.getHeroClass().equalsIgnoreCase(playerClass)) {
                valid.add(c);
            }
        }
        if (valid.isEmpty()) return null;

        Random random = new Random();
        return valid.get(random.nextInt(valid.size()));
    }
}

