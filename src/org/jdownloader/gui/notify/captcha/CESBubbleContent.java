package org.jdownloader.gui.notify.captcha;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import jd.gui.swing.jdgui.components.IconedProcessIndicator;
import net.miginfocom.swing.MigLayout;

import org.appwork.swing.MigPanel;
import org.appwork.swing.components.ExtButton;
import org.appwork.utils.StringUtils;
import org.appwork.utils.formatter.TimeFormatter;
import org.appwork.utils.images.IconIO;
import org.appwork.utils.swing.SwingUtils;
import org.jdownloader.actions.AppAction;
import org.jdownloader.captcha.v2.Challenge;
import org.jdownloader.captcha.v2.SolverStatus;
import org.jdownloader.captcha.v2.challenge.recaptcha.v2.RecaptchaV2Challenge;
import org.jdownloader.captcha.v2.challenge.stringcaptcha.ImageCaptchaChallenge;
import org.jdownloader.captcha.v2.solver.CESChallengeSolver;
import org.jdownloader.captcha.v2.solver.CESSolverJob;
import org.jdownloader.captcha.v2.solverjob.SolverJob;
import org.jdownloader.gui.notify.AbstractBubbleContentPanel;
import org.jdownloader.gui.notify.BubbleNotify;
import org.jdownloader.gui.notify.Element;
import org.jdownloader.gui.notify.gui.CFG_BUBBLE;
import org.jdownloader.gui.translate._GUI;
import org.jdownloader.updatev2.gui.LAFOptions;

public class CESBubbleContent extends AbstractBubbleContentPanel {
    private JLabel                   status;
    private long                     startTime;
    private JLabel                   duration;
    private CESChallengeSolver<?>    solver;
    private CESSolverJob<?>          job;
    private JLabel                   statusLbl;
    private JLabel                   timeoutLbl;
    private JLabel                   durationLbl;
    private ExtButton                button;
    private CESBubble                bubble;
    private SolverStatus             latestStatus;
    private JLabel                   creditsLabel;
    private JLabel                   credits;
    protected IconedProcessIndicator progressCircle = null;

    public CESBubbleContent(final CESChallengeSolver<?> solver, final CESSolverJob<?> cesSolverJob, int timeoutms) {
        super(solver.getService().getIcon(20));
        this.solver = solver;
        this.job = cesSolverJob;
        startTime = System.currentTimeMillis();
        // super("ins 0,wrap 2", "[][grow,fill]", "[grow,fill]");
        setLayout(new MigLayout("ins 3 3 0 3,wrap 2", "[fill][grow,fill]", "[]"));
        // , _GUI.T.balloon_reconnect_start_msg(), new AbstractIcon(IconKey.ICON_RECONNECT, 32)
        MigPanel east = new MigPanel("ins 0 0 0 0,wrap 2", "[fill][grow,fill]", "[]");
        east.setOpaque(false);
        east.add(timeoutLbl = new JLabel(_GUI.T.CESBubbleContent_CESBubbleContent_wait(TimeFormatter.formatMilliSeconds(timeoutms, 0), solver.getService().getName())), "hidemode 3,spanx");
        timeoutLbl.setForeground(LAFOptions.getInstance().getColorForErrorForeground());
        SwingUtils.toBold(timeoutLbl);
        east.add(durationLbl = createHeaderLabel((_GUI.T.ReconnectDialog_layoutDialogContent_duration())), "hidemode 3");
        east.add(duration = new JLabel(""), "hidemode 3");
        east.add(creditsLabel = createHeaderLabel((_GUI.T.CESBubbleContent_CESBubbleContent_credits())), "hidemode 3");
        east.add(credits = new JLabel(""), "hidemode 3");
        east.add(statusLbl = createHeaderLabel((_GUI.T.CESBubbleContent_CESBubbleContent_status())), "hidemode 3");
        east.add(status = new JLabel(""), "hidemode 3");
        progressCircle = createProgress(solver.getService().getIcon(20));
        add(progressCircle, "width 32!,height 32!,pushx,growx,pushy,growy,aligny top");
        add(east);
        if (CFG_BUBBLE.CFG.isCaptchaExchangeSolverBubbleImageVisible() && !(cesSolverJob.getChallenge() instanceof RecaptchaV2Challenge)) {
            Challenge<?> ic = cesSolverJob.getChallenge();
            if (ic instanceof ImageCaptchaChallenge) {
                try {
                    ImageIcon icon = null;
                    icon = new ImageIcon(((ImageCaptchaChallenge) ic).getAnnotatedImage());
                    if (icon.getIconWidth() > 300 || icon.getIconHeight() > 300) {
                        icon = new ImageIcon(IconIO.getScaledInstance(icon.getImage(), 300, 300));
                    }
                    add(new JSeparator(), "spanx,pushx,growx");
                    add(new JLabel(icon), "spanx,pushx,growx");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        progressCircle.setIndeterminate(true);
        progressCircle.setValue(0);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                ((CESChallengeSolver<Object>) solver).kill((SolverJob<Object>) cesSolverJob.getJob());
                if (bubble != null) {
                    bubble.hideBubble(0);
                }
            }
        });
        add(button = new ExtButton(new AppAction() {
            {
                setName(_GUI.T.lit_cancel());
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ((CESChallengeSolver<Object>) solver).kill((SolverJob<Object>) cesSolverJob.getJob());
                if (bubble != null) {
                    bubble.hideBubble(0);
                }
            }
        }), "hidemode 3,spanx,pushx,growx");
        updateTimer(timeoutms);
    }

    @Override
    public void stop() {
        final IconedProcessIndicator progressCircle = this.progressCircle;
        if (progressCircle != null) {
            progressCircle.setIndeterminate(false);
            progressCircle.setMaximum(100);
            progressCircle.setValue(100);
        }
        super.stop();
        update();
        button.setVisible(false);
    }

    public void update() {
        SolverStatus s = job.getStatus();
        if (s != null) {
            latestStatus = s;
        }
        credits.setText(solver.getAccountStatusString());
        if (latestStatus == null) {
            status.setVisible(false);
            statusLbl.setVisible(false);
        } else {
            status.setVisible(true);
            statusLbl.setVisible(true);
            status.setText(latestStatus.getLabel());
            status.setIcon(latestStatus.getIcon());
        }
        duration.setText(TimeFormatter.formatMilliSeconds(System.currentTimeMillis() - startTime, 0));
    }

    @Override
    public void updateLayout() {
    }

    public static void fill(ArrayList<Element> elements) {
    }

    public void updateTimer(final long rest) {
        update();
        timeoutLbl.setText(_GUI.T.CESBubbleContent_CESBubbleContent_wait(TimeFormatter.formatMilliSeconds(rest, 0), solver.getService().getName()));
        button.setVisible(true);
        timeoutLbl.setVisible(rest > 0);
        durationLbl.setVisible(rest <= 0);
        duration.setVisible(rest <= 0);
        statusLbl.setVisible(rest <= 0);
        status.setVisible(rest <= 0);
        creditsLabel.setVisible(StringUtils.isNotEmpty(credits.getText()));
        credits.setVisible(StringUtils.isNotEmpty(credits.getText()));
        if (bubble != null) {
            bubble.pack();
            BubbleNotify.getInstance().relayout();
        }
    }

    public void setBubble(CESBubble cesBubble) {
        bubble = cesBubble;
    }
}
