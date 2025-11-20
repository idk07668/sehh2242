import java.awt.*;
import javax.swing.*;

public class VirtualKeypad extends JPanel {
    private JTextField target;
    private Runnable enterAction;

    // Backwards-compatible constructor
    public VirtualKeypad(JTextField target, Runnable enterAction) {
        this.target = target;
        this.enterAction = enterAction;
        init();
    }

    // New constructor: no fixed target (will be set dynamically)
    public VirtualKeypad(Runnable enterAction) {
        this.target = null;
        this.enterAction = enterAction;
        init();
    }

    public void setTarget(JTextField t) {
        this.target = t;
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel grid = new JPanel(new GridLayout(4, 3, 5, 5));
        for (int i = 1; i <= 9; i++) {
            int digit = i;
            JButton b = new JButton(String.valueOf(digit));
            b.addActionListener(e -> {
                if (target != null) target.setText(target.getText() + digit);
            });
            grid.add(b);
        }

        // decimal point button for floating point input
        JButton dot = new JButton(".");
        dot.addActionListener(e -> {
            if (target != null) {
                String s = target.getText();
                if (!s.contains(".")) {
                    if (s.length() == 0) target.setText("0.");
                    else target.setText(s + ".");
                }
            }
        });
        grid.add(dot);

        JButton zero = new JButton("0");
        zero.addActionListener(e -> { if (target != null) target.setText(target.getText() + "0"); });
        grid.add(zero);

        JButton back = new JButton("Delete");
        back.addActionListener(e -> {
            if (target != null) {
                String t = target.getText();
                if (t.length() > 0) target.setText(t.substring(0, t.length() - 1));
            }
        });
        grid.add(back);

        add(grid, BorderLayout.CENTER);

        JButton enter = new JButton("Enter");
        enter.addActionListener(e -> { if (enterAction != null) enterAction.run(); });
        add(enter, BorderLayout.SOUTH);
    }
}
