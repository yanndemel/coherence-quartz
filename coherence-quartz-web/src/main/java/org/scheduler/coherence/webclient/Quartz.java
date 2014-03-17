package org.scheduler.coherence.webclient;


import java.util.ArrayList;
import java.util.List;

import org.scheduler.coherence.model.CronTrigger;
import org.scheduler.coherence.model.SimpleTrigger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.hiperf.common.ui.client.i18n.NakedConstants;
import com.hiperf.common.ui.client.model.PersistentWrapperListModel;
import com.hiperf.common.ui.client.model.WrapperListModel;
import com.hiperf.common.ui.client.widget.ViewHelper;
import com.hiperf.common.ui.shared.WrapperContext;
import com.hiperf.common.ui.shared.model.ScreenConfig;

public class Quartz implements EntryPoint {

	private List<Class> classes;
	
	public void onModuleLoad() {
		/* Création du contexte */
		GWT.create(WrapperContext.class);
		
		classes = new ArrayList<Class>();
		/* Classes du modèle */
		classes.add(SimpleTrigger.class);
		classes.add(CronTrigger.class);
		
		
		/* Classe de gestion des configurations d'écran */
		classes.add(ScreenConfig.class);
		
		TabPanel rootPanel = new TabPanel();
		RootPanel.get().add(rootPanel);
		addTables(rootPanel);
		
	}

	private void addTables(final TabPanel rootPanel) {
		int i = 0;
		for(Class c : classes) {
			final int j = i;
			final String name = c.getName();
			final String sn = name.substring(name.lastIndexOf(".") + 1);
			WrapperContext.addView(name, sn, sn);
			final SimplePanel panel = new SimplePanel();
			rootPanel.add(panel, sn);
			rootPanel.addSelectionHandler(new SelectionHandler<Integer>() {
				
				@Override
				public void onSelection(SelectionEvent<Integer> event) {
					if(event.getSelectedItem().equals(j)) {
						panel.clear();
						WrapperListModel rows = new PersistentWrapperListModel(name);
						ViewHelper.displayRootTableForUser(panel, rows, true, sn);
					}
				}
			});
			i++;
		}
		ViewHelper.showWaitPopup(NakedConstants.constants.loadingScreens());
		WrapperContext.loadScreensConfiguration(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				ViewHelper.hideWaitPopup();
				rootPanel.selectTab(0, true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				ViewHelper.hideWaitPopup(caught);
			}
		});
		
	}

}
