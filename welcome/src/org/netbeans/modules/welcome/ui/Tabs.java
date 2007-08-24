/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.welcome.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.welcome.content.Constants;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import org.netbeans.modules.welcome.WelcomeOptions;
import org.netbeans.modules.welcome.content.Utils;
import org.openide.util.Utilities;

/**
 *
 * @author S. Aubrecht
 */
class Tabs extends JPanel implements Constants {

    private JScrollPane leftComp;
    private JScrollPane rightComp;
    private JComponent leftTab;
    private JComponent rightTab;
    private JPanel tabContent;
    
    private Image imgStripWest;
    private Image imgStripCenter;
    private Image imgStripEast;
    
    public Tabs( String leftTabTitle, JComponent leftTab, 
            String rightTabTitle, final JComponent rightTab) {
        
        super( new BorderLayout() );
        setOpaque( false );

        this.leftTab = leftTab;
        this.rightTab = rightTab;
        
        this.imgStripCenter = Utilities.loadImage( IMAGE_STRIP_BOTTOM_CENTER );
        this.imgStripWest = Utilities.loadImage( IMAGE_STRIP_BOTTOM_WEST );
        this.imgStripEast = Utilities.loadImage( IMAGE_STRIP_BOTTOM_EAST );
        
        final JToggleButton leftButton = new TabToggleButton( leftTabTitle );
        final JToggleButton rightButton = new TabToggleButton( rightTabTitle );
        
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isLeftTabSelected = e.getSource() == leftButton;
                leftButton.setSelected( isLeftTabSelected );
                rightButton.setSelected( !isLeftTabSelected );
                switchTab( isLeftTabSelected );
            }
        };
        
        leftButton.addActionListener( al );
        rightButton.addActionListener( al );
        
        JPanel buttons = new JPanel( new GridBagLayout() );
        buttons.setOpaque(false);
        buttons.add( new Tab( leftButton, true ), new GridBagConstraints(0,0,1,1,1.0,0.0,
                GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0) );
        buttons.add( new Tab( rightButton, false ), new GridBagConstraints(1,0,1,1,1.0,0.0,
                GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0) );
        
        add( buttons, BorderLayout.NORTH );
        
        tabContent = new JPanel( new CardLayout() );
        tabContent.setOpaque( false );

        add( tabContent, BorderLayout.CENTER );
        boolean selectLeftTab = WelcomeOptions.getDefault().getLastActiveTab() == 0;
        leftButton.setSelected( selectLeftTab );
        rightButton.setSelected( !selectLeftTab );
        switchTab( selectLeftTab );
    }

    private void switchTab( boolean showLeftTab ) {
        JScrollPane compToShow = showLeftTab ? leftComp : rightComp;
        JScrollPane compToHide = showLeftTab ? rightComp : leftComp;

        if( null == compToShow ) {
            compToShow = new JScrollPane( showLeftTab ? leftTab : rightTab );
            compToShow.setOpaque( false );
            compToShow.getViewport().setOpaque( false );
            compToShow.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
            compToShow.setBorder( BorderFactory.createEmptyBorder() );

            if( showLeftTab ) {
                leftComp = compToShow;
                tabContent.add( leftComp, "left" ); //NOI18N
            } else {
                rightComp = compToShow;
                tabContent.add( rightComp, "right" ); //NOI18N
            }
        }

        if( null != compToHide )
            compToHide.setVisible( false );
        
        compToShow.setVisible( true );
        
        invalidate();
        revalidate();
        repaint();

        WelcomeOptions.getDefault().setLastActiveTab( showLeftTab ? 0 : 1 );
    }
    
    private static class Tab extends JPanel {
        private JToggleButton button;
        private boolean isLeftTab;
        private Image imgUnselBottom;
        private Image imgSelLeft;
        private Image imgSelUpperLeft;
        private Image imgSelLowerLeft;
        private Image imgSelRight;
        private Image imgSelUpperRight;
        private Image imgSelLowerRight;
        
        public Tab( final JToggleButton button, boolean isLeftTab ) {
            super( new GridBagLayout() );
            this.button = button;
            this.isLeftTab = isLeftTab;
            imgUnselBottom = Utilities.loadImage( IMAGE_TAB_UNSEL );
            if( isLeftTab ) {
                imgSelLeft = Utilities.loadImage( IMAGE_TAB_SEL_RIGHT );
                imgSelUpperLeft = Utilities.loadImage( IMAGE_TAB_SEL_UPPER_RIGHT );
                imgSelLowerLeft = Utilities.loadImage( IMAGE_TAB_SEL_LOWER_RIGHT );
            } else {
                imgSelRight = Utilities.loadImage( IMAGE_TAB_SEL_LEFT );
                imgSelUpperRight = Utilities.loadImage( IMAGE_TAB_SEL_UPPER_LEFT );
                imgSelLowerRight = Utilities.loadImage( IMAGE_TAB_SEL_LOWER_LEFT );
            }
            button.addChangeListener( new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    button.setForeground( Utils.getColor( button.isSelected() 
                            ? COLOR_TAB_SEL_FOREGROUND 
                            : COLOR_TAB_UNSEL_FOREGROUND ) );
                    repaint();
                }
            });
            add( button, new GridBagConstraints(0,0,1,1,1.0,1.0,
                    GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,5,4,0),0,0) );
            button.setBorder( BorderFactory.createEmptyBorder() );
            button.setBorderPainted( false );
            button.setOpaque( false );
            button.setFont( TAB_FONT );
            button.setForeground( Utils.getColor( button.isSelected() 
                    ? COLOR_TAB_SEL_FOREGROUND 
                    : COLOR_TAB_UNSEL_FOREGROUND ) );
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            boolean isSelected = button.isSelected();
            g.setColor( Utils.getColor( isSelected ? COLOR_SCREEN_BACKGROUND : COLOR_TAB_UNSEL_BACKGROUND ) );
            int width = getWidth();
            int height = getHeight();
            
            g.fillRect( 0, 0, width, height );
            
            if( isSelected ) {
                if( isLeftTab ) {
                    
                    g.setColor( Utils.getColor( COLOR_TAB_UNSEL_BACKGROUND ) );
                    g.fillRect( width-imgSelUpperLeft.getWidth(null), 0, width, height );
                    
                    int rightImageWidth = imgSelLeft.getWidth(null);
                    g.drawImage(imgSelUpperLeft, width-imgSelUpperLeft.getWidth(null), 0, null);
                    for( int i=0; i<(height-imgSelUpperLeft.getHeight(null)-imgSelLowerLeft.getHeight(null)); i++ ) {
                        g.drawImage( imgSelLeft, width-rightImageWidth, imgSelUpperLeft.getHeight(null)+i, null);
                    }
                    g.drawImage(imgSelLowerLeft, width-imgSelLowerLeft.getWidth(null), height-imgSelLowerLeft.getHeight(null), null);
                    
                } else {
                    
                    g.setColor( Utils.getColor( COLOR_TAB_UNSEL_BACKGROUND ) );
                    g.fillRect( 0, 0, imgSelUpperRight.getWidth(null), height );
                    
                    g.drawImage(imgSelUpperRight, 0, 0, null);
                    for( int i=0; i<(height-imgSelUpperRight.getHeight(null)-imgSelLowerRight.getHeight(null)); i++ ) {
                        g.drawImage( imgSelRight, 0, imgSelUpperRight.getHeight(null)+i, null);
                    }
                    g.drawImage(imgSelLowerRight, 0, height-imgSelLowerRight.getHeight(null), null);
                }
            } else {
                int imgWidth = imgUnselBottom.getWidth(null);
                int imgHeight = imgUnselBottom.getHeight(null);
                for( int i=0; i<width/imgWidth+1; i++ ) {
                    g.drawImage( imgUnselBottom, i*imgWidth, height-imgHeight, null );
                }
            }
        }

        @Override
        protected void paintBorder(Graphics g) {
        }
    }
    
    private class TabToggleButton extends JToggleButton {
        public TabToggleButton( String label ) {
            super( label );
        }

        @Override
        public void updateUI() {
            super.updateUI();
            setBorderPainted( false );
            setBorder( BorderFactory.createEmptyBorder() );
            setContentAreaFilled( false );
            setRolloverEnabled( getRolloverIcon() != null );
            setOpaque(false);
            setFocusPainted(false);
        }
    }
}
