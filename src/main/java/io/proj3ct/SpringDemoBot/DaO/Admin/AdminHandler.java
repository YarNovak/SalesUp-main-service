package io.proj3ct.SpringDemoBot.DaO.Admin;

import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.Admin.AdminUtils;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface AdminHandler extends MessageHandle{

	/**
	 * Implement this instead of {@code support} when you need admin-only handlers.
	 * The default {@code support} implementation will check admin status and
	 * delegate here.
	 */
	boolean supportAdmin(Message msgcallbackData);

	@Override
	default boolean support(Message msgcallbackData){
		// If admin check passes - delegate to implementing class's supportAdmin
		if(AdminUtils.isAdmin(msgcallbackData)){
			return supportAdmin(msgcallbackData);
		}
		// Not admin: do not support the message
		return false;
	}

}
