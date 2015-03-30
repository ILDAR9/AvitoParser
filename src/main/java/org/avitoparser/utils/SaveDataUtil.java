package org.avitoparser.utils;

import org.avitoparser.model.*;
import org.avitoparser.pageparser.AdvertPageParser.Param;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SaveDataUtil {

    private static final Properties propAvito = PropertiesSingelton.getInstance();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(propAvito.getProperty("avito.page_parse.date_format"));
    private static final Logger logger = LoggerFactory.getLogger(SaveDataUtil.class);

    private static final String CITY_NAME = "Казань";
    private static final String HS_TYPE_KIRPICH = "кирпичного дома",
            HS_TYPE_PANEL = "панельного дома",
            HS_TYPE_BLOCK = "блочного дома",
            HS_TYPE_MONOLIT = "монолитного дома",
            HS_TYPE_WOOD = "деревянного дома";

    private City city;
    private Map<String, HouseType> houseTypeMap;

    private Session session;
    private SessionFactory sessionFactory;

    private List<?> fetchTable(String tableName, String rowName, String rowValue) {
        Query query = session.createQuery("from " + tableName + " where " + rowName + " = :row_value").
                setParameter("row_value", rowValue);
        return query.list();

    }

    private void prepareHouseType() {
        houseTypeMap = new HashMap<>();
        List<?> list = null;
        HouseType houseType = null;
        List<String> houseTypeList = new LinkedList<String>() {{
            add(HS_TYPE_BLOCK);
            add(HS_TYPE_KIRPICH);
            add(HS_TYPE_MONOLIT);
            add(HS_TYPE_PANEL);
            add(HS_TYPE_WOOD);
        }};
        for (String houseTypeName : houseTypeList) {
            list = fetchTable("HouseType", "hs_type_name", houseTypeName);
            if (DataUtils.isEmpty(list)) {
                session.beginTransaction();
                houseType = new HouseType(houseTypeName);
                session.persist(houseType);
                session.getTransaction().commit();
            } else {
                houseType = (HouseType) list.get(0);
            }
            houseTypeMap.put(houseTypeName, houseType);
        }

    }


    private void prepareCity() {
        List<?> list = fetchTable("City", "city_name", CITY_NAME);
        if (DataUtils.isEmpty(list)) {
            session.beginTransaction();
            city = new City(CITY_NAME);
            session.persist(city);
            session.getTransaction().commit();
        } else {
            city = (City) list.get(0);
        }
    }

    public SaveDataUtil(boolean forUpdate) {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
                applySettings(configuration.getProperties());
        sessionFactory = configuration.buildSessionFactory(builder.build());
        startSession();

        if (!forUpdate) {
            prepareCity();
            prepareHouseType();
        }

    }

    public void startSession() {
        session = sessionFactory.openSession();
    }

    public void closeSession() {
        session.close();
    }

    /* for testing */
    public Map<Param, String> initData() {
        Map<Param, String> resMap = new HashMap<>();
        resMap.put(Param.ADDRESS, "Пушкина 5, кв 77");
        resMap.put(Param.AREA, "120");
        resMap.put(Param.AVITO_ID, "234234234");
        resMap.put(Param.CITY, "Казань");
        resMap.put(Param.DATE_POST, "Feb/20/2015 13:23"); //Repair
        resMap.put(Param.DESCRIPTION, "Продаю, тороплюсь, просто ВАХ!");
        resMap.put(Param.FLOOR_COUNT, "9");
        resMap.put(Param.FLOOR_CURRENT, "3");
        resMap.put(Param.HOUSE_TYPE, "Кирпичного дома");
        resMap.put(Param.PERSON_NAME, "Ильдар");
        resMap.put(Param.PHONE_CODE, "+79272345932");
        resMap.put(Param.PRICE, "1503000");
        resMap.put(Param.ROOM_COUNT, "2");
        return resMap;
    }


    public void saveData(Map<Param, String> resMap) {

        session.beginTransaction();
        try {

            Person person = new Person(resMap.get(Param.PERSON_NAME), resMap.get(Param.PHONE_CODE));
            session.persist(person);

            Apartment ap = new Apartment();
            ap.setAddress(resMap.get(Param.ADDRESS));
            ap.setArea(Integer.parseInt(resMap.get(Param.AREA)));
            ap.setCurrentFloor(Integer.parseInt(resMap.get(Param.FLOOR_CURRENT)));
            ap.setFloorCount(Integer.parseInt(resMap.get(Param.FLOOR_COUNT)));
            ap.setRoomCount(Integer.parseInt(resMap.get(Param.ROOM_COUNT)));
            ap.setHouseType(houseTypeMap.get(resMap.get(Param.HOUSE_TYPE)));
            ap.setCity(city);
            session.persist(ap);

            Add add = new Add();

            add.setAvitoIdentificator(Long.parseLong(resMap.get(Param.AVITO_ID)));

            add.setDatePost(DATE_FORMAT.parse(resMap.get(Param.DATE_POST)));
            add.setDescription(resMap.get(Param.DESCRIPTION));
            add.setPerson(person);
            add.setPrise(Integer.parseInt(resMap.get(Param.PRICE)));
            add.setApartments(ap);
            session.persist(add);
            session.getTransaction().commit();
            System.out.println(add.getId());
        } catch (ParseException | NumberFormatException e) {
            session.getTransaction().rollback();
            logger.error("", e);
        }

    }

    public void updatePhoneNumbers(Map<String, String> phoneMap) {
        for (Map.Entry<String, String> phoneEntry : phoneMap.entrySet()) {
            if (!(DataUtils.isEmpty(phoneEntry.getKey()) || DataUtils.isEmpty(phoneEntry.getValue())) && phoneEntry.getKey().endsWith(".png")) {
                session.beginTransaction();
                System.out.println(phoneEntry);
                session.createQuery("update Person set person_telephone = :value where person_telephone = :key").
                        setParameter("key", phoneEntry.getKey()).
                        setParameter("value", phoneEntry.getValue()).
                        executeUpdate();
                session.getTransaction().commit();

            }

        }
        System.out.println("Done!!!!");
        session.close();
    }

    public static void main(String[] args) throws Exception {
        SaveDataUtil saveDataUtil = new SaveDataUtil(true);
        Map<String, String> map = new HashMap<>();
        map.put("337494963.png", "8 962 559-57-67");
        saveDataUtil.updatePhoneNumbers(map);
//        Map<Param, String> resMap = saveDataUtil.initData();
//        saveDataUtil.saveData(resMap);
//        System.out.println("-----------------------------Done N1");
//        resMap.put(Param.PRICE, "101");
//        resMap.put(Param.AVITO_ID, "23");
//        resMap.put(Param.ADDRESS, "test");
//        resMap.put(Param.PHONE_CODE, "+78237547234");
//        saveDataUtil.saveData(resMap);
//        System.out.println("-----------------------------Done N2");
    }
}
