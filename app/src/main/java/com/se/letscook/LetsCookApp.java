package com.se.letscook;

import android.app.Application;

public class LetsCookApp extends Application
{

    private static RepositoryInterface repo;

    public RepositoryInterface getRepository()
    {
        if (repo == null)
        {
            this.createRepoInstance();
        }
        return repo;
    }

    private synchronized void createRepoInstance()
    {
        repo = OnlineRepository.getRepository(getApplicationContext());
    }

}
