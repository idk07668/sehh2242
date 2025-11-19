import java.awt.*;
import javax.swing.*;

public class VirtualKeypad extends JPanel {
    private JTextField target;
    private Runnable enterAction;

    public VirtualKeypad(JTextField target, Runnable enterAction) {
        this.target = target;
        this.enterAction = enterAction;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel grid = new JPanel(new GridLayout(4, 3, 5, 5));
        for (int i = 1; i <= 9; i++) {
            int digit = i;
            JButton b = new JButton(String.valueOf(digit));
            b.addActionListener(e -> target.setText(target.getText() + digit));
            grid.add(b);
        }

        JButton clear = new JButton("C");
        clear.addActionListener(e -> target.setText(""));
        grid.add(clear);

        JButton zero = new JButton("0");
        zero.addActionListener(e -> target.setText(target.getText() + "0"));
        grid.add(zero);

        JButton back = new JButton("<- ");
        back.addActionListener(e -> {
            String t = target.getText();
            if (t.length() > 0) target.setText(t.substring(0, t.length() - 1));
        });
        grid.add(back);

        add(grid, BorderLayout.CENTER);

        JButton enter = new JButton("Enter");
        enter.addActionListener(e -> {
            if (enterAction != null) enterAction.run();
        });
        add(enter, BorderLayout.SOUTH);
    }
}
