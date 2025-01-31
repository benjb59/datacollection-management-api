package fr.insee.survey.datacollectionmanagement.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.javafaker.Animal;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.repository.AddressRepository;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Owner;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Support;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.OwnerRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SupportRepository;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SurveyRepository;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;

@Component
public class Dataloader {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyUnitRepository surveyUnitRepository;

    @Autowired
    private QuestioningRepository questioningRepository;

    @Autowired
    private QuestioningAccreditationRepository questioningAccreditationRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private PartitioningRepository partitioningRepository;

    @PostConstruct
    public void init() {

        Faker faker = new Faker();
        EasyRandom generator = new EasyRandom();

        initContact(faker);
        initMetadata(faker, generator);
        initQuestionning(faker, generator);

    }

    private void initContact(Faker faker) {
        for (int i = 0; i < 10000; i ++ ) {
            final Contact c = new Contact();
            final Address a = new Address();

            Name n = faker.name();
            String name = n.lastName();
            String firstName = n.firstName();
            com.github.javafaker.Address fakeAddress = faker.address();

            a.setCountryName(fakeAddress.country());
            a.setStreetNumber(fakeAddress.buildingNumber());
            a.setStreetName(fakeAddress.streetName());
            a.setZipCode(fakeAddress.zipCode());
            a.setCity(fakeAddress.cityName());
            addressRepository.save(a);

            c.setIdentifier(RandomStringUtils.randomAlphanumeric(7).toUpperCase());
            c.setLastName(name);
            c.setFirstName(firstName);
            c.setPhone(faker.phoneNumber().phoneNumber());
            c.setGender(Contact.Gender.male);
            c.setFunction(faker.job().title());
            c.setComment(faker.beer().name());
            c.setEmail(name + "." + firstName + "@cocorico.fr");
            c.setAddress(a);
            contactRepository.save(c);
        }
    }

    private void initMetadata(Faker faker, EasyRandom generator2) {

        int year = 2022;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        Date dateEndOfYear = calendar.getTime();

        Owner ownerInsee = new Owner();
        ownerInsee.setLabel("Insee");
        Set<Source> setSourcesInsee = new HashSet<>();

        Owner ownerAgri = new Owner();
        ownerAgri.setLabel("SSM Agriculture");
        Set<Source> setSourcesSsp = new HashSet<>();

        Support supportInseeHdf = new Support();
        supportInseeHdf.setLabel("Insee Hauts-de-France");
        Set<Source> setSourcesSupportInsee = new HashSet<>();

        Support supportSsne = new Support();
        supportSsne.setLabel("Insee Normandie - SSNE");
        Set<Source> setSourcesSupportSsne = new HashSet<>();

        for (int i = 0; i < 10; i ++ ) {
            Source source = new Source();
            Animal animal = faker.animal();
            String animalName = animal.name().toUpperCase();
            source.setIdSource(animalName);
            source.setLongWording("Have you ever heard about " + animalName + " ?");
            source.setShortWording("Source about " + animalName);
            source.setPeriodicity("M");
            sourceRepository.save(source);
            Set<Survey> setSurveys = new HashSet<>();
            if (i % 2 == 0)
                setSourcesInsee.add(source);
            else {
                setSourcesSsp.add(source);
            }

            if (i % 3 == 0)
                setSourcesSupportInsee.add(source);
            else {
                setSourcesSupportSsne.add(source);
            }

            for (int j = 0; j < 4; j ++ ) {

                Survey survey = new Survey();
                String id = animalName + (year - j);
                survey.setId(id);
                survey.setYear(year - j);
                survey.setLongObjectives("The purpose of this survey is to find out everything you can about "
                    + animalName + ". Your response is essential to ensure the quality and reliability of the results of this survey.");
                survey.setShortObjectives("All about " + id);
                survey.setCommunication("Communication around " + id);
                survey.setSpecimenUrl("http://specimenUrl/" + id);
                survey.setDiffusionUrl("http://diffusion/" + id);
                survey.setCnisUrl("http://cnis/" + id);
                survey.setNoticeUrl("http://notice/" + id);
                survey.setVisaNumber(year + RandomStringUtils.randomAlphanumeric(6).toUpperCase());
                survey.setLongWording("Survey " + animalName + " " + (year - j));
                survey.setShortWording(id);
                survey.setSampleSize(Integer.parseInt(RandomStringUtils.randomNumeric(5)));
                setSurveys.add(survey);
                surveyRepository.save(survey);
                Set<Campaign> setCampaigns = new HashSet<>();

                for (int k = 0; k < 11; k ++ ) {
                    Campaign campaign = new Campaign();
                    int month = k + 1;
                    String period = "M" + month;
                    campaign.setYear(year - j);
                    campaign.setPeriod(period);
                    campaign.setCampaignId(animalName + (year - j) + period);
                    campaign.setCampaignWording("Campaign about " + animalName + " in " + (year - j) + " and period " + period);
                    setCampaigns.add(campaign);
                    campaignRepository.save(campaign);
                    Set<Partitioning> setParts = new HashSet<>();

                    for (int l = 0; l < 3; l ++ ) {

                        Partitioning part = new Partitioning();
                        part.setId(animalName + (year - j) + "M" + month + "-00" + l);
                        Date openingDate = faker.date().past(90, 0, TimeUnit.DAYS);
                        Date closingDate = faker.date().between(openingDate, dateEndOfYear);
                        Date returnDate = faker.date().between(openingDate, closingDate);
                        Date today = new Date();

                        part.setOpeningDate(openingDate);
                        part.setClosingDate(closingDate);
                        part.setReturnDate(returnDate);
                        part.setStatus(today.compareTo(closingDate) < 0 ? "open" : "closed");
                        setParts.add(part);
                        part.setCampaign(campaign);
                        partitioningRepository.save(part);
                    }
                    campaign.setSurvey(survey);
                    campaign.setPartitionings(setParts);
                    campaignRepository.save(campaign);

                }
                survey.setSource(source);
                survey.setCampaigns(setCampaigns);
                surveyRepository.save(survey);
            }
            source.setSurveys(setSurveys);
            sourceRepository.save(source);

        }
        ownerInsee.setSources(setSourcesInsee);
        ownerAgri.setSources(setSourcesSsp);
        ownerRepository.saveAll(Arrays.asList(new Owner[] {
            ownerInsee, ownerAgri
        }));

        supportInseeHdf.setSources(setSourcesSupportInsee);
        supportSsne.setSources(setSourcesSupportSsne);
        supportRepository.saveAll(Arrays.asList(new Support[] {
            supportInseeHdf, supportSsne
        }));

    }

    private void initQuestionning(Faker faker, EasyRandom generator) {

        for (int i = 0; i < 10000; i ++ ) {
            SurveyUnit su = new SurveyUnit();
            Questioning qu = new Questioning();
            Set<QuestioningAccreditation> questioningAccreditations = new HashSet<>();

            String fakeSiren = RandomStringUtils.randomNumeric(9);
            su.setIdSu(fakeSiren);
            su.setCompanyName(faker.company().name());
            su.setSurveyUnitId(fakeSiren);

            Set<Questioning> setQuestioning = new HashSet<>();
            qu.setModelName("m" + RandomStringUtils.randomNumeric(2));
            qu.setIdPartitioning(partitioningRepository.findRandomPartitioning().getId());
            surveyUnitRepository.save(su);
            qu.setSurveyUnit(su);
            questioningRepository.save(qu);
            setQuestioning.add(qu);
            su.setQuestionings(setQuestioning);

            for (int j = 0; j < 2; j ++ ) {
                QuestioningAccreditation accreditation = new QuestioningAccreditation();
                accreditation.setIdContact(contactRepository.findRandomContact().getIdentifier());
                accreditation.setQuestioning(qu);
                questioningAccreditations.add(accreditation);
                questioningAccreditationRepository.save(accreditation);
            }
            qu.setQuestioningAccreditations(questioningAccreditations);
            questioningRepository.save(qu);
            surveyUnitRepository.save(su);
        }
    }
}
