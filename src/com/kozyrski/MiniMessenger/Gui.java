package com.kozyrski.MiniMessenger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Gui {
    JTextPane incoming;
    JList guests;
    JTextPane outgoing;
    BufferedReader reader;
    PrintWriter writer;
    String nickName = "";
    ClientMain clientMain;
    JComboBox emojiBox;
    Image img;
    SimpleAttributeSet attributeSetForOutgoing;
    SimpleAttributeSet attributeSetForIncoming;
    StyledDocument styledDocument;
    JComboBox colorBox;
    BufferedImage backgroundImage;
    ArrayList<Style> styles = new ArrayList<>();

    public void createGui() {

        try {
            backgroundImage = ImageIO.read(new File("src\\066.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JFrame mainFrame = new JFrame("MiniMessenger");
        MyPanel mainPanel = new MyPanel();
        JPanel scrollsPanel = new JPanel();
        JPanel utilPanel = new JPanel();
        JPanel outgoingPanel = new JPanel();
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        scrollsPanel.setLayout(gridBagLayout);
        utilPanel.setLayout(gridBagLayout);
        JButton sendButton = new JButton("Send");
        outgoing = new JTextPane();
        emojiBox = new JComboBox();
        guests = new JList(new DefaultListModel<>());
        emojiBox.setModel(loadEmoticonsIntoComboBox());
        emojiBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outgoing.setText(outgoing.getText() + ":)");
                outgoing.insertIcon(new ImageIcon(emojiBox.getSelectedItem().toString()));

            }
        });
        attributeSetForOutgoing = new SimpleAttributeSet();
        attributeSetForIncoming = new SimpleAttributeSet();
        colorBox = new JComboBox();
        colorBox.setModel(loadColorPallete());
        colorBox.setPreferredSize(new Dimension(40, 25));
        colorBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (colorBox.getSelectedItem().toString()) {
                    case "/src/blue.jpg":
                        StyleConstants.setForeground(attributeSetForOutgoing, Color.BLUE);
                        styles.add(Style.BLUE);
                        break;
                    case "/src/green.jpg":
                        styles.add(Style.GREEN);
                        StyleConstants.setForeground(attributeSetForOutgoing, Color.GREEN);
                        break;
                    case "/src/yellow.jpg":
                        styles.add(Style.YELLOW);
                        StyleConstants.setForeground(attributeSetForOutgoing, Color.YELLOW);
                        break;
                    case "/src/black.jpg":
                        styles.add(Style.BLACK);
                        StyleConstants.setForeground(attributeSetForOutgoing, Color.BLACK);
                        break;
                }
                outgoing.getStyledDocument().setCharacterAttributes(0, outgoing.getText().length(),
                        attributeSetForOutgoing, false);
            }
        });
        JButton boldButton = new JButton(new ImageIcon("/src/boldButton.png"));
        boldButton.setPressedIcon(new ImageIcon("/src/boldButtonSelected.png"));

        boldButton.setPreferredSize(new Dimension(25, 25));
        boldButton.setContentAreaFilled(false);
        boldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (StyleConstants.isBold(attributeSetForOutgoing)) {
                    StyleConstants.setBold(attributeSetForOutgoing, false);
                    styles.remove(Style.BOLD);
                    boldButton.setIcon(new ImageIcon("/src/boldButton.png"));
                } else {
                    StyleConstants.setBold(attributeSetForOutgoing, true);
                    styles.add(Style.BOLD);
                    boldButton.setIcon(new ImageIcon("/src/boldButtonSelected.png"));

                }
                outgoing.getStyledDocument().setCharacterAttributes(0, outgoing.getText().length(),
                        attributeSetForOutgoing, false);
            }
        });
        JButton italicButton = new JButton(new ImageIcon("/src/italicButton.png"));
        italicButton.setPreferredSize(new Dimension(25, 25));
        italicButton.setContentAreaFilled(false);
        italicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (StyleConstants.isItalic(attributeSetForOutgoing)) {
                    StyleConstants.setItalic(attributeSetForOutgoing, false);
                    styles.remove(Style.ITALIC);
                } else {
                    StyleConstants.setItalic(attributeSetForOutgoing, true);
                    styles.add(Style.ITALIC);
                }
                outgoing.getStyledDocument().setCharacterAttributes(0, outgoing.getText().length(),
                        attributeSetForOutgoing, false);
            }
        });
        JButton underlineButton = new JButton(new ImageIcon("/src/underlineButton.png"));
        underlineButton.setPreferredSize(new Dimension(25, 25));
        underlineButton.setContentAreaFilled(false);
        underlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (StyleConstants.isUnderline(attributeSetForOutgoing)) {
                    StyleConstants.setUnderline(attributeSetForOutgoing, false);
                    styles.remove(Style.UNDERLINE);
                } else {
                    StyleConstants.setUnderline(attributeSetForOutgoing, true);
                    styles.add(Style.UNDERLINE);
                }
                outgoing.getStyledDocument().setCharacterAttributes(0, outgoing.getText().length(),
                        attributeSetForOutgoing, false);
            }
        });


        JButton clearHistoryButton = new JButton("Clear");
        clearHistoryButton.setPreferredSize(new Dimension(65, 25));
        clearHistoryButton.setContentAreaFilled(false);
        clearHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                incoming.setText("");
            }
        });
        sendButton.addActionListener(new SendButtonListener());
        mainFrame.getRootPane().setDefaultButton(sendButton);
        clientMain = new ClientMain(this, nickName);
        writer = clientMain.getWriter();
        reader = clientMain.getReader();
        nickName = JOptionPane.showInputDialog("Enter your name", "guest");
        writer.println(nickName);
        writer.flush();
        incoming = new JTextPane();
        incoming.setPreferredSize(new Dimension(300, 250));
        incoming.setEditable(false);


        JScrollPane scroller = new JScrollPane(incoming);
        JScrollPane guestsScrollPane = new JScrollPane(guests);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        guestsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        guestsScrollPane.setPreferredSize(new Dimension(140, 255));
        scroller.setPreferredSize(new Dimension(450, 255));
        gbc.gridx = 0;
        gbc.gridy = 0;
        scrollsPanel.add(scroller, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        scrollsPanel.add(guestsScrollPane, gbc);
        outgoing.setPreferredSize(new Dimension(400, 30));
        outgoingPanel.add(outgoing);
        outgoingPanel.add(sendButton);

        gbc.gridx = 1;
        gbc.gridy = 0;
        utilPanel.add(boldButton, gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        emojiBox.setPreferredSize(new Dimension(50, 25));
        utilPanel.add(italicButton, gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        utilPanel.add(underlineButton, gbc);
        gbc.gridx = 4;
        gbc.gridy = 0;
        utilPanel.add(colorBox, gbc);
        gbc.gridx = 5;
        gbc.gridy = 0;
        utilPanel.add(emojiBox, gbc);
        gbc.gridx = 6;
        gbc.gridy = 0;
        utilPanel.add(clearHistoryButton, gbc);
        gbc.gridy = 0;
        gbc.gridx = 0;
        mainPanel.setPreferredSize(new Dimension(700, 500));
        mainFrame.getContentPane().add(BorderLayout.NORTH, mainPanel);
        mainPanel.setLayout(gridBagLayout);
        mainPanel.add(scrollsPanel);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(utilPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(outgoingPanel, gbc);
        SwingUtilities.updateComponentTreeUI(mainPanel);
        mainFrame.setSize(700, 500);
        mainFrame.setVisible(true);
    }

    public void addMessage(String string) throws BadLocationException {
        boolean containsEmoticons = false;
        SimpleAttributeSet emot = new SimpleAttributeSet();
        styledDocument = incoming.getStyledDocument();
        StyleConstants.setBold(attributeSetForIncoming, false);
        StyleConstants.setItalic(attributeSetForIncoming, false);
        switch (string) {
            case "<BOLD>":
                string = string.replace("<BOLD>", "");
                StyleConstants.setBold(attributeSetForIncoming, true);
                break;
            case "<ITALIC>":
                string = string.replace("<ITALIC>", "");
                StyleConstants.setItalic(attributeSetForIncoming, true);
                break;
            case "<UNDERLINE>":
                string = string.replace("<UNDERLINE>", "");
                StyleConstants.setUnderline(attributeSetForIncoming, true);
                break;
            case "<GREEN>":
                string = string.replace("<GREEN>", "");
                StyleConstants.setForeground(attributeSetForIncoming, Color.GREEN);
                break;
            case "<BLUE>":
                string = string.replace("<BLUE>", "");
                StyleConstants.setForeground(attributeSetForIncoming, Color.BLUE);
                break;
            case "<RED>":
                string = string.replace("<RED>", "");
                StyleConstants.setForeground(attributeSetForIncoming, Color.RED);
                break;
            case "<BLACK>":
                string = string.replace("<BLACK>", "");
                StyleConstants.setForeground(attributeSetForIncoming, Color.BLACK);
                break;
            case ":)":
                emot = new SimpleAttributeSet();
                StyleConstants.setIcon(emot, new ImageIcon("/src/happy.png"));
                containsEmoticons = true;
                break;
            case "]:)":
                emot = new SimpleAttributeSet();
                StyleConstants.setIcon(emot, new ImageIcon("/src/devil.png"));
                containsEmoticons = true;
                break;
            case ":(":
                emot = new SimpleAttributeSet();
                StyleConstants.setIcon(emot, new ImageIcon("/src/sad.png"));
                containsEmoticons = true;
                break;
        }

        String[] stringArray = (string.split(":\\)"));
        for (int i = 0; i < stringArray.length; i++) {
            styledDocument.insertString(styledDocument.getLength(), stringArray[i], attributeSetForIncoming);
            if (containsEmoticons) {
                if (string.endsWith(":)")) {
                    styledDocument.insertString(styledDocument.getLength(), stringArray[i], emot);

                } else if (!string.endsWith(":)") && i != stringArray.length - 1) {
                    styledDocument.insertString(styledDocument.getLength(), stringArray[i], emot);
                }
            }
        }
        styledDocument.insertString(styledDocument.getLength(), "\n", attributeSetForIncoming);
    }

    public void addGuest(String[] nickNames) {
        guests.setListData(nickNames);
    }

    private DefaultComboBoxModel<Icon> loadEmoticonsIntoComboBox() {
        DefaultComboBoxModel<Icon> model = new DefaultComboBoxModel<>();
        model.addElement(new ImageIcon("/src/happy.png"));
        model.addElement(new ImageIcon("/src/devil.png"));
        model.addElement(new ImageIcon("/src/sad.png"));

        return model;
    }

    private DefaultComboBoxModel<Icon> loadColorPallete() {
        DefaultComboBoxModel<Icon> model = new DefaultComboBoxModel<>();
        model.addElement(new ImageIcon("/src/blue.jpg"));
        model.addElement(new ImageIcon("/src/black.jpg"));
        model.addElement(new ImageIcon("/src/green.jpg"));
        model.addElement(new ImageIcon("/src/yellow.jpg"));
        return model;
    }

    private String getTagsForStyles(ArrayList list) {
        String tags = "";
        if (list.contains(Style.BOLD)) {
            tags = tags + "<BOLD>";
        } else if (list.contains(Style.ITALIC)) {
            tags = tags + "<ITALIC>";
        } else if (list.contains(Style.UNDERLINE)) {
            tags = tags + "<UNDERLINE>";
        } else if (list.contains(Style.GREEN)) {
            tags = tags + "<GREEN>";
        } else if (list.contains(Style.BLACK)) {
            tags = tags + "<BLACK>";
        } else if (list.contains(Style.BLUE)) {
            tags = tags + "<BLUE>";
        } else if (list.contains(Style.RED)) {
            tags = tags + "<RED>";
        }
        return tags;
    }

    public class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                writer.println(getTagsForStyles(styles) + nickName + ": " + outgoing.getText());
                writer.flush();
                styles.removeAll(styles);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }

    class ImagePanel extends JPanel {

        public void paintComponent(Graphics g) {

            super.paintComponent(g);
            g.drawImage(img, 0, 0, this);

            g.dispose();
        }

    }

    class MyPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, null);
        }
    }

}

