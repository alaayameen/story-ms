package com.social.story.trash.operatios;

import com.social.story.trash.utils.Constants;

public class ContentCommandExecutor {

    Command abstractCommand;
    Constants.ID_TYPE idType;

    public ContentCommandExecutor(Command abstractCommand, Constants.ID_TYPE idType) {
        this.abstractCommand = abstractCommand;
        this.idType = idType;
    }

    public void execute() {
        abstractCommand.execute(idType);
    }
}
