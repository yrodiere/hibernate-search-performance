package org.hibernate.performance.search.lucene;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.tck.TckBackendHelperFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance( TestInstance.Lifecycle.PER_CLASS )
public class SearchingIT {

	private SessionFactory sessionFactory;
	private ModelService modelService;

	@BeforeAll
	public void beforeAll() throws Exception {
		sessionFactory = HibernateORMHelper.buildSessionFactory( TckBackendHelperFactory.manualProperties() );
		modelService = TckBackendHelperFactory.getModelService();

		new DomainDataFiller( sessionFactory ).fillData( 0 );
		try ( Session session = ( sessionFactory.openSession() ) ) {
			modelService.massIndexing( session );
		}
	}

	@AfterAll
	public void afterAll() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Test
	public void company() {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<Company> companies = modelService.search( session, Company.class, "legalName", "Company0" );
			assertThat( companies ).hasSize( 1 );

			// no match
			companies = modelService.search( session, Company.class, "legalName", "CompanyX" );
			assertThat( companies ).isEmpty();

			// indexEmbedded match
			companies = modelService.search( session, Company.class, "businessUnits.name", "Unit7" );
			assertThat( companies ).hasSize( 1 );

			// search by id
			companies = modelService.search( session, Company.class, "id", 0 );
			assertThat( companies ).hasSize( 1 );
		}
	}

	@Test
	public void businessUnit() {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<BusinessUnit> businessUnits = modelService.search( session, BusinessUnit.class, "name", "Unit7" );
			assertThat( businessUnits ).hasSize( 1 );

			// no match
			businessUnits = modelService.search( session, BusinessUnit.class, "name", "UnitX" );
			assertThat( businessUnits ).isEmpty();

			// indexEmbedded match
			businessUnits = modelService.search( session, BusinessUnit.class, "owner.legalName", "Company0" );
			assertThat( businessUnits ).hasSize( 10 );
		}
	}

	@Test
	public void employee() {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			// match
			List<Employee> employees = modelService.search( session, Employee.class, "name", "name77" );
			assertThat( employees ).hasSize( 1 );

			// no match
			employees = modelService.search( session, Employee.class, "name", "nameX" );
			assertThat( employees ).isEmpty();

			// count
			long count = modelService.count( session, Employee.class, "surname", "surname77" );
			assertThat( count ).isEqualTo( 1 );

			// range
			employees = modelService.range( session, Employee.class, "socialSecurityNumber",
					"socialSecurityNumber32", "socialSecurityNumber41" );
			assertThat( employees ).extracting( "id" )
					.containsExactlyInAnyOrder( 32, 33, 34, 35, 36, 37, 38, 39, 4, 40, 41 );

			// indexEmbedded match
			count = modelService.count( session, Employee.class, "company.legalName", "Company0" );
			assertThat( count ).isEqualTo( 100 );

			// projection
			List<Object> ids = modelService.projectId( session, Employee.class, "businessUnit.name", "Unit7" );
			assertThat( ids ).containsExactlyInAnyOrder( 70, 71, 72, 73, 74, 75, 76, 77, 78, 79 );
		}
	}

}