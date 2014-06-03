package com.internalaudit.client.presenter;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.internalaudit.client.InternalAuditServiceAsync;
import com.internalaudit.client.event.MainEvent;
import com.internalaudit.client.view.LoadingPopup;
import com.internalaudit.shared.Exceptions;
import com.internalaudit.shared.User;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.TextField;


public class LoginPresenter implements Presenter 

{
	private final InternalAuditServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	private LoadingPopup loadingPopup = null;
	
	public interface Display 
	{
		Widget asWidget();
		Object getHtmlErrorMessage = null;
		
		PasswordField getTxtPassword();
		TextField getTxtUserName();
		HasClickHandlers getBtnSubmit();
//		ListBox getListYears();
		Label getLblError();
	}  

	public LoginPresenter(InternalAuditServiceAsync rpcService, HandlerManager eventBus, Display view) 
	{
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
	}

	public void go(HasWidgets container) 
	{
		container.clear();
		container.add(display.asWidget());
		bind();
	}

	private void bind() {
		
		
		
		RootPanel.get("loadingMessage").setVisible(false);
		
		display.getBtnSubmit().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if(display.getTxtUserName().getText().equals("")|| display.getTxtPassword().getText().equals("")){
					display.getLblError().setVisible(true);
					display.getLblError().setText("username / password cannot be empty");
				}else{
					loadingPopup = new LoadingPopup();
					loadingPopup.display();
					signIn(display.getTxtUserName().getText(),display.getTxtPassword().getText());
					
				}
			}

			
		});

	}

	private void sendEmailNotifications() {
		rpcService.fetchJobExceptions(0, new AsyncCallback<ArrayList<Exceptions>>(){

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(ArrayList<Exceptions> result) {
				
			}});
		
	}

	public void signIn(String userName,String password)
	{

		rpcService.signIn(userName,password, new AsyncCallback<User>()
				

				{
			public void onFailure(Throwable caught) {
				Window.alert(caught.getStackTrace().toString());
			}

			public void onSuccess(User user) {
				if(user != null) 
				{
					display.getLblError().setVisible(false);
					eventBus.fireEvent(new MainEvent(user));
				}
				else 
				{
					display.getLblError().setVisible(true);
					display.getLblError().setText("username / password invalid");
				}
				if(loadingPopup!=null){
					loadingPopup.remove();
				}
				
			}
				});

	}
	
	


}


