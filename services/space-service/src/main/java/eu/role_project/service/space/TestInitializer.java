package eu.role_project.service.space;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import eu.role_project.service.resource.ROLETerms;

import se.kth.csc.kmr.conserve.Concept;
import se.kth.csc.kmr.conserve.Request;
import se.kth.csc.kmr.conserve.Resolution;
import se.kth.csc.kmr.conserve.core.AbstractInitializer;
import se.kth.csc.kmr.conserve.core.ConserveTerms;
import se.kth.csc.kmr.conserve.iface.internal.RequestNotifier;
import se.kth.csc.kmr.conserve.iface.jaxrs.RequestImpl;

public class TestInitializer extends AbstractInitializer {

	// private static Logger log =
	// LoggerFactory.getLogger(TestInitializer.class);

	@Inject
	@Named("conserve.user.context")
	private UUID userContextUuid;

	@Inject
	@Named("conserve.user.predicate")
	private UUID userPredicateUuid;

	public static final UUID space1_id = UUID
			.fromString("6b329f05-f5bd-4702-b8ca-6fb45de385a1");
	public static final UUID space2_id = UUID
			.fromString("4647ae5b-f75d-4d81-a634-5ba54ccbc546");

	public static final UUID tool1_id = UUID
			.fromString("4af91b4d-c670-407c-ad6b-8f9474ccf8c8");
	public static final UUID tool2_id = UUID
			.fromString("df381e94-0f0d-423a-911c-9a3e28961ef1");
	public static final UUID tool3_id = UUID
			.fromString("0d4a10a5-0412-41fb-ba85-32281b323b67");
	public static final UUID tool4_id = UUID
			.fromString("795f95cf-ec73-4b4c-ab52-bae8bce32939");

	public static final UUID user1_id = UUID
			.fromString("b6762683-e025-4bf6-ad65-83310e4a3e3e");
	public static final UUID member1_id = UUID
			.fromString("7af530bb-e351-4d64-97e3-c8195ae1d369");
	public static final UUID owner1_id = UUID
			.fromString("58efa76f-9f67-47c1-aba4-3ed8e7f743d9");
	public static final UUID member2_id = UUID
			.fromString("fc0a98a1-432e-498b-ad82-88d58c99561d");
	public static final UUID owner2_id = UUID
			.fromString("c56d5948-7b17-4345-90f4-762a71d1b175");

	public static final UUID user_default_id = UUID
			.fromString("e4008a52-cf77-4e58-953a-65dc2d9c567f");

	@Inject
	@Named("password-salt")
	private String salt;

	// @Inject
	// private ShindigDbService shindigDb;

	@Inject
	private RequestNotifier requestNotifier;

	@Override
	public void initialize(Request request) {
		// Space 1
		Concept space1 = store().in(SpaceService.ID).sub(ROLETerms.space)
				.acquire(space1_id, "test");
		store().in(space1)
				.as(ConserveTerms.metadata)
				.type("application/json")
				.string("{\"\": { \"http://purl.org/dc/terms/title\": [{ \"value\": \"Test Space\", \"type\": \"literal\" }]}}");
		requestNotifier.setResolution(Resolution.StandardType.CONTEXT,
				store.getConcept(SpaceService.ID));
		requestNotifier.setResolution(Resolution.StandardType.CREATED, space1);
		requestNotifier.doPost();

		// Tool 1
		Concept tool1 = store().in(space1).sub(ROLETerms.tool)
				.acquire(tool1_id);
		store().in(tool1)
				.as(ConserveTerms.metadata)
				.type("application/json")
				.string("{\"\": { \"http://purl.org/dc/terms/title\": [{ \"value\": \"RSS\", \"type\": \"literal\" }]}}");
		store().in(tool1)
				.put(ConserveTerms.reference,
						"http://role-project.svn.sourceforge.net/viewvc/role-project/trunk/gadgets/rss/gadget.xml");
		store().in(tool1).put(ConserveTerms.type,
				"http://purl.org/role/terms/OpenSocialGadget");

		// Test user
		Concept user1 = store().in(userContextUuid).sub(userPredicateUuid)
				.acquire(user1_id, "testuser");
		store().in(user1)
				.as(ConserveTerms.metadata)
				.type("application/json")
				.string("{\"\": { \"http://purl.org/dc/terms/title\": [{ \"value\": \"Test User\", \"type\": \"literal\" }]}}");
		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] digest = sha1.digest(("roleabdc" + salt).getBytes());
		store().in(user1).as(ConserveTerms.secret)
				.type("application/octet-stream").bytes(digest);
		requestNotifier.setResolution(Resolution.StandardType.CONTEXT,
				store.getConcept(userContextUuid));
		requestNotifier.setResolution(Resolution.StandardType.CREATED, user1);
		requestNotifier.doPost();

		// Default user
		Concept defaultUser = store().in(userContextUuid).sub(userPredicateUuid)
				.acquire(user_default_id);
		store().in(defaultUser)
			.as(ConserveTerms.metadata)
			.type("application/json")
			.string("{\"\": { \"http://purl.org/dc/terms/title\": [{ \"value\": \"Anonymous\", \"type\": \"literal\" }]}}");
		
		// // User in Shindig
		// try {
		// JSONArray people = shindigDb.getDb().getJSONArray("people");
		// JSONObject jsonUser = new JSONObject(
		// "{\"id\" : \""
		// + Base64UUID.encode(user1_id)
		// +
		// "\",\"displayName\" : \"Test User\",\"gender\" : \"female\",\"hasApp\" : true,\"name\" : {\"familyName\" : \"User\",\"givenName\" : \"Test\",\"formatted\" : \"Test User\"}}");
		// people.put(jsonUser);
		// JSONObject data = shindigDb.getDb().getJSONObject("data");
		// JSONObject jsonData = new JSONObject("{\"count\" : \"0\"}");
		// data.put(Base64UUID.encode(user1_id), jsonData);
		// } catch (JSONException e) {
		// log.error("Error adding test user to Shindig JSON DB", e);
		// }
		// // shindigEntityManager.getTransaction().begin();
		// // shindigEntityManager
		// // .createNativeQuery(
		// //
		// "insert into Person (person_id, display_name, drinker, gender, network_presence, smoker) values (?i, ?n, ?dr, ?ge, ?ne, ?sm)")
		// // .setParameter("i", "111").setParameter("n", "Test User")
		// // .setParameter("dr", "NO").setParameter("ge", "FEMALE")
		// // .setParameter("ne", "OFFLINE").setParameter("sm", "NO")
		// // .executeUpdate();
		// // shindigEntityManager.getTransaction().commit();

		// Space 2
		Concept space2 = store().in(SpaceService.ID).sub(ROLETerms.space)
				.acquire(space2_id, "test2");
		store().in(space2)
				.as(ConserveTerms.metadata)
				.type("application/json")
				.string("{\"\": { \"http://purl.org/dc/terms/title\": [{ \"value\": \"Test2 Space\", \"type\": \"literal\" }]}}");
		requestNotifier.setResolution(Resolution.StandardType.CONTEXT,
				store.getConcept(SpaceService.ID));
		requestNotifier.setResolution(Resolution.StandardType.CREATED, space2);
		requestNotifier.doPost();

		// Tool 3
		Concept tool3 = store().in(space2).sub(ROLETerms.tool)
				.acquire(tool3_id);
		UriBuilder urib = ((RequestImpl) request).getUriInfo()
				.getBaseUriBuilder();
		urib.path("spaces/test2/role:widget");
		store().in(tool3)
				.as(ConserveTerms.metadata)
				.type("application/json")
				.string("{\"\": { \"http://purl.org/dc/terms/title\": [{ \"value\": \"Space\", \"type\": \"literal\" }]}}");
		store().in(tool3).put(ConserveTerms.reference, urib.build().toString());
		store().in(tool3).put(ConserveTerms.type,
				"http://purl.org/role/terms/OpenSocialGadget");

		// Member 1
		Concept member1 = store().in(space1).sub(ROLETerms.member)
				.acquire(member1_id);
		store().in(member1).put(ConserveTerms.reference, user1_id);
		requestNotifier.setResolution(Resolution.StandardType.CONTEXT, space1);
		requestNotifier.setResolution(Resolution.StandardType.CREATED, member1);
		requestNotifier.doPost();

		// Owner 1
		Concept owner1 = store().in(space1).sub(ConserveTerms.owner)
				.acquire(owner1_id);
		store().in(owner1).put(ConserveTerms.reference, user1_id);
		requestNotifier.setResolution(Resolution.StandardType.CONTEXT, space1);
		requestNotifier.setResolution(Resolution.StandardType.CREATED, owner1);
		requestNotifier.doPost();

		// Member 2
		Concept member2 = store().in(space2).sub(ROLETerms.member)
				.acquire(member2_id);
		store().in(member2).put(ConserveTerms.reference, user1_id);
		requestNotifier.setResolution(Resolution.StandardType.CONTEXT, space2);
		requestNotifier.setResolution(Resolution.StandardType.CREATED, member2);
		requestNotifier.doPost();

		// Owner 2
		Concept owner2 = store().in(space2).sub(ConserveTerms.owner)
				.acquire(owner2_id);
		store().in(owner2).put(ConserveTerms.reference, user1_id);
		requestNotifier.setResolution(Resolution.StandardType.CONTEXT, space2);
		requestNotifier.setResolution(Resolution.StandardType.CREATED, owner2);
		requestNotifier.doPost();

		// Tool 2
		Concept tool2 = store().in(user1).sub(ROLETerms.tool).acquire(tool2_id);
		store().in(tool2)
				.as(ConserveTerms.metadata)
				.type("application/json")
				.string("{\"\": { \"http://purl.org/dc/terms/title\": [{ \"value\": \"Portfolio\", \"type\": \"literal\" }]}}");
		store().in(tool2)
				.put(ConserveTerms.reference,
						"http://role-project.svn.sourceforge.net/viewvc/role-project/trunk/gadgets/portfolio/gadget.xml");
		store().in(tool2).put(ConserveTerms.type,
				"http://purl.org/role/terms/OpenSocialGadget");

		// Tool 4
		Concept tool4 = store().in(defaultUser).sub(ROLETerms.tool)
				.acquire(tool4_id);
		store().in(tool4)
				.as(ConserveTerms.metadata)
				.type("application/json")
				.string("{\"\": { \"http://purl.org/dc/terms/title\": [{ \"value\": \"Translator\", \"type\": \"literal\" }]}}");
		store().in(tool4)
				.put(ConserveTerms.reference,
						"http://role-project.svn.sourceforge.net/viewvc/role-project/trunk/gadgets/language/src/main/webapp/translator/translator.xml");
		store().in(tool4).put(ConserveTerms.type,
				"http://purl.org/role/terms/OpenSocialGadget");
	}
}