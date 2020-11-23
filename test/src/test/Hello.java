package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

//인스턴스 시작
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;

public class Hello {

	final static AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

	public static void list_instance() {
		// 인스턴스 목록 나열
		boolean done = false;

		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while (!done) {
			DescribeInstancesResult response = ec2.describeInstances(request);

			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					System.out.printf(
							"Found instance with id %s, " + "AMI %s, " + "type %s, " + "state %s "
									+ "and monitoring state %s",
							instance.getInstanceId(), instance.getImageId(), instance.getInstanceType(),
							instance.getState().getName(), instance.getMonitoring().getState());
				}
				System.out.println("");
			}

			request.setNextToken(response.getNextToken());

			if (response.getNextToken() == null) {
				done = true;
			}
		}

		// 인스턴스 목록나열
	}

	public static void start_instance() {
		// 인스턴스 시작
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter instance id : ");
		String id = sc.next();
		StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(id);

		ec2.startInstances(request);

		// 인스턴스 시작 마무리
	}

	public static void stop_instance() {
		// 인스턴스 중지

		Scanner sc = new Scanner(System.in);
		System.out.print("Enter instance id : ");
		String id = sc.next();

		StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(id);

		ec2.stopInstances(request);

		// 인스턴스 중지
	}

	public static void reboot_instance() {
		// 인스턴스 재부팅

		Scanner sc = new Scanner(System.in);
		System.out.print("Enter instance id : ");
		String id = sc.next();

		// final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

		RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(id);

		RebootInstancesResult response = ec2.rebootInstances(request);

		// 인스턴스 재부팅
	}

	public static void available_zones() {
		// 가용 영역 설명

		DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();

		for (AvailabilityZone zone : zones_response.getAvailabilityZones()) {
			System.out.printf("Found availability zone %s " + "with status %s " + "in region %s", zone.getZoneName(),
					zone.getState(), zone.getRegionName());
			System.out.println("");
		}

	}

	public static void available_regions() {
		// region 설명

		DescribeRegionsResult regions_response = ec2.describeRegions();

		for (Region region : regions_response.getRegions()) {
			System.out.printf("Found region %s " + "with endpoint %s", region.getRegionName(), region.getEndpoint());
			System.out.println("");
		}

	}

	public static void create_instance() {
		// 인스턴스 생성
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter instance name : ");
		String name = sc.next();
		System.out.println("Enter ami_id : ");
		String ami_id = sc.next();
		// String ami_id = "ami-08e60325d631d2ff6";

		RunInstancesRequest run_request = new RunInstancesRequest().withImageId(ami_id)
				.withInstanceType(InstanceType.T2Micro).withMaxCount(1).withMinCount(1);

		RunInstancesResult run_response = ec2.runInstances(run_request);

		String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

		Tag tag = new Tag().withKey("Name").withValue(name);

		CreateTagsRequest tag_request = new CreateTagsRequest().withResources(reservation_id).withTags(tag);

		CreateTagsResult tag_response = ec2.createTags(tag_request);

		// 인스턴스 생성
	}

	// public image 리스트 출력
	public static void public_image_list() {
		DescribeImagesRequest request = new DescribeImagesRequest().withFilters(new ArrayList<>());
		DescribeImagesResult describeImagesResult = ec2.describeImages(request);

		describeImagesResult.getImages().forEach(image -> {
			System.out.println(image.getImageId());
		});

	}

	// list image 출력
	public static void list_images() {
		Filter filter = new Filter();
		filter.setName("owner-id");
		filter.setValues(Arrays.asList(new String[] { "893305173367" }));
		DescribeImagesRequest request = new DescribeImagesRequest().withFilters(filter);
		DescribeImagesResult describeImagesResult = ec2.describeImages(request);

		describeImagesResult.getImages().forEach(image -> {
			System.out.println(image.getImageId());
		});
	}

	// main 시작
	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		while (true) {

			int num;
			System.out.println("------------------------------------------------------------");
			System.out.println("1. list instance                2. available zones");
			System.out.println("3. start instance               4. available regions");
			System.out.println("5. stop instance                6. create instance");
			System.out.println("7. reboot instance              8. list images");
			System.out.println("9. public image list            10. quit");
			System.out.println("------------------------------------------------------------");

			System.out.println("Enter an integer : ");

			num = sc.nextInt();

			switch (num) {
			case 1:
				list_instance();
				break;
			case 2:
				available_zones();
				break;
			case 3:
				start_instance();
				break;
			case 4:
				available_regions();
				break;
			case 5:
				stop_instance();
				break;
			case 6:
				create_instance();
				break;
			case 7:
				reboot_instance();
				break;
			case 8:
				list_images();
				break;
			case 9:
				public_image_list();
				break;
			case 10:
			default:
				System.exit(0);
			
			}

		}

	}

}
