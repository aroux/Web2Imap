package com.engine.da;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;

import com.engine.da.entities.MailAccount;
import com.engine.da.entities.Message;

public class DBManager {

	private static DBManager self = null;
	
	private SessionFactory sessionFactory;

	private DBManager() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	public static DBManager getInstance() {
		if (self == null) self = new DBManager();
		return self;
	}
	
	public void createNewAccount(String emailAddress) {
		MailAccount mAccount = new MailAccount(emailAddress); 
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			session.save(mAccount);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		session.close();
	}
	
	public void saveMessage(Message message) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		try {
			session.save(message);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			throw e;
		}
		session.close();
	}
	
	public Message getMessage(Integer uid) {
		Message entity = new Message();
		entity.setUid(uid);
		Session session = sessionFactory.openSession();
		Criteria criteria = session.createCriteria(Message.class).add(Example.create(entity));
		@SuppressWarnings("unchecked")
		List<Message> list = criteria.list();
		session.close();
		if (list.size() < 1) return null;
		return list.get(0);
	}
	
	public Long getLastMessageWebuid() {
		Session session = sessionFactory.openSession();
		//session.beginTransaction();
		Criteria criteria = session.createCriteria(Message.class);
		criteria.setProjection(Projections.max("webmailuid"));
		@SuppressWarnings("unchecked")
		List<Long> result = criteria.list();
		session.close();
		if (result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}
}
