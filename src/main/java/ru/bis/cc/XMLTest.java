package ru.bis.cc;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;

public class XMLTest {
    public static void main(String[] args) throws JAXBException {
        Cat cat = new Cat("Murka", 3);
        StringWriter writer = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(Cat.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(cat, writer);
        System.out.println(writer);
    }

    @XmlRootElement
    private static class Cat {
        @XmlElement
        private String name;
        @XmlElement
        private int age;

        public Cat() {

        }

        public Cat(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}


