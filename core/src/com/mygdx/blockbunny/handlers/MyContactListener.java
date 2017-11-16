package com.mygdx.blockbunny.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by mkemp on 11/8/17.
 */

public class MyContactListener implements ContactListener {

    private int numFootContacts;

    @Override
    public void beginContact(Contact contact) {
        System.out.println("Begin contact");
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getUserData() != null && a.getUserData().equals("foot")) {
            System.out.println("a is foot");
            numFootContacts++;
        }

        if (b.getUserData() != null && b.getUserData().equals("foot")) {
            System.out.println("b is foot");
            numFootContacts++;
        }

        System.out.println(a.getUserData() + ", " + b.getUserData());
    }

    @Override
    public void endContact(Contact contact) {
        System.out.println("End contact");

        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if (a.getUserData() != null && a.getUserData().equals("foot")) {
            System.out.println("a is foot");
            numFootContacts--;
        }

        if (b.getUserData() != null && b.getUserData().equals("foot")) {
            System.out.println("b is foot");
            numFootContacts--;
        }
    }

    public boolean isPlayerOnGround() {
        return numFootContacts > 0;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
