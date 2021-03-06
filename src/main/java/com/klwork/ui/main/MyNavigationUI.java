/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.klwork.ui.main;

import com.klwork.explorer.ui.mainlayout.ExplorerLayout;
import com.klwork.ui.DiscoveryNavigator;
import com.klwork.ui.security.ShiroSecurityNavigator;
import com.vaadin.annotations.Theme;
import com.vaadin.server.*;
import com.vaadin.ui.*;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * @author ww
 */
@Component
@Scope("prototype")
@Theme(ExplorerLayout.THEME)
public class MyNavigationUI extends UI implements ErrorHandler
{
    @Override
    protected void init(final VaadinRequest request)
    {
        VaadinSession.getCurrent().setErrorHandler(this);
        setSizeFull();

        ShiroSecurityNavigator navigator = new ShiroSecurityNavigator(this, this);
    }

    /**
     * Exception on action
     */
    @Override
    public void error(com.vaadin.server.ErrorEvent event)
    {
        // connector event
        if (event.getThrowable().getCause() instanceof IllegalArgumentException)
        {
            IllegalArgumentException exception = (IllegalArgumentException) event.getThrowable().getCause();
            Notification.show(exception.getMessage(), Notification.Type.ERROR_MESSAGE);

            // Cleanup view. Now Vaadin ignores errors and always shows the view.  :-(
            // since beta10
            setContent(null);
            return;
        }

        // Error on page load. Now it doesn't work. User sees standard error page.
        if (event.getThrowable() instanceof IllegalArgumentException)
        {
            IllegalArgumentException exception = (IllegalArgumentException) event.getThrowable();

            Label label = new Label(exception.getMessage());
            label.setWidth(-1, Unit.PERCENTAGE);

            Link goToMain = new Link("Go to main", new ExternalResource("/"));

            VerticalLayout layout = new VerticalLayout();
            layout.addComponent(label);
            layout.addComponent(goToMain);
            layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
            layout.setComponentAlignment(goToMain, Alignment.MIDDLE_CENTER);

            VerticalLayout mainLayout = new VerticalLayout();
            mainLayout.setSizeFull();
            mainLayout.addComponent(layout);
            mainLayout.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);

            setContent(mainLayout);
            Notification.show(exception.getMessage(), Notification.Type.ERROR_MESSAGE);
            return;
        }

        DefaultErrorHandler.doDefault(event);
    }
}
