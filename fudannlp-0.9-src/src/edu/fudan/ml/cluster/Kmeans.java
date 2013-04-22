package edu.fudan.ml.cluster;
import java.util.ArrayList;
import java.util.Iterator;
import edu.fudan.ml.types.Instance;
import edu.fudan.ml.types.InstanceSet;
import edu.fudan.ml.types.SparseVector;
public class Kmeans {
	int k;
	private final double TOL = 0.0;
	public SparseVector[] centroids = null;
	private SparseVector[] newCentroids = null;
	private ArrayList<Instance>[] assignedClusters = null;
	private ArrayList<Instance>[] newClusters = null;
	private double[] clusterQualities = null;
	private double[] newQualities = null;
	int maxIterations = 10;
	public Kmeans (int k) {
		this.k = k;
		this.centroids = new SparseVector[k];
		this.assignedClusters = new ArrayList[k];
		this.clusterQualities = new double[k];
		this.newCentroids = new SparseVector[k];
		this.newClusters = new ArrayList[k];
		this.newQualities = new double[k];
	}
	private SparseVector calculateCentroid (ArrayList<Instance> insts) {
		SparseVector centroid = new SparseVector();
		Iterator i = insts.iterator();
		while (i.hasNext()) {
			Instance d = (Instance) i.next();
			centroid.plus((SparseVector) d.getData());
		}
		centroid.scalarDivide(insts.size());
		return centroid;
	}
	private double calculateClusterQuality (ArrayList<Instance> docs,
			SparseVector centroid) {
		double quality = 0.0;
		SparseVector c = centroid;
		for (int i = 0; i < docs.size(); ++i) {
			Instance doc = docs.get(i);
			quality += c.distanceSquared((SparseVector) doc.getData());
		}
		return quality;
	}
	private double calculatePartitionQuality (ArrayList<Instance>[] docs,
			SparseVector[] centroid) {
		double quality = 0.0;
		for (int i = 0; i < docs.length; ++i) {
			quality += this.calculateClusterQuality(docs[i], centroid[i]);
		}
		return quality;
	}
	public void cluster (ArrayList<Instance> insts) {
		System.out.println("Initial centers");
		for(int i=0;i<k;i++){
			assignedClusters[i] = new ArrayList<Instance>();
		}
		for(int i=0;i<insts.size();i++){
			assignedClusters[i%k].add(insts.get(i));
		}
		for(int i=0;i<k;i++){
			centroids[i] = calculateCentroid(assignedClusters[i]);
			clusterQualities[i] = calculateClusterQuality(assignedClusters[i], centroids[i]);
		}
		for (int numChanged = 0, itr = 0; (numChanged > 0) || (itr == 0); ++itr) {
			numChanged = 0;
			while (true) {
				int numReassigned = doBatchKmeans();
				System.out.println("After an iteration of Batch K-Means, " +
						numReassigned + " documents were moved.");
				double oldQuality = 0.0;
				double newQuality = 0.0;
				for (int b = 0; b < this.centroids.length; ++b) {
					oldQuality += this.clusterQualities[b];
					newQuality += this.newQualities[b];
				}
				double qualityDelta = oldQuality - newQuality;
				System.out.println("Change in quality is: " + qualityDelta);
				if (qualityDelta < this.TOL) {
					System.out.println(
							"Benefit of change is below tolerance... Switching to incremental...\n");
					break;
				}
				if (numReassigned == 0) {
					System.out.println(
							"Batch K-Means has made no changes! Switching to incremental...\n");
					break;
				}
				for (int kk = 0; kk < this.assignedClusters.length; ++kk) {
					this.assignedClusters[kk] = this.newClusters[kk];
					this.centroids[kk] = this.newCentroids[kk];
					this.clusterQualities[kk] = this.newQualities[kk];
				}
				numChanged = numReassigned;
			}
			double qual = 0.0;
			for (int i = 0; i < this.clusterQualities.length; ++i) {
				qual += this.clusterQualities[i];
			}
			System.out.println("Quality of partition generated by Batch K-Means: " +
					qual);
		}
		System.out.println("Batch K-Means Complete!\n");
	}
	private int doBatchKmeans () {
		System.out.println("\nBegining a new iteration of K-Means...");
		int numReassigned = 0;
		
		for (int i = 0; i < this.centroids.length; ++i) {
			this.newClusters[i] = new ArrayList<Instance>();
			this.newCentroids[i] = new SparseVector();
			this.newQualities[i] = 0.0;
		}
		for (int clusterNum = 0; clusterNum < this.centroids.length; ++clusterNum) {
			for (int docNum = 0; docNum < this.assignedClusters[clusterNum].size();	++docNum) {
				Instance doc = this.assignedClusters[clusterNum].get(docNum);
				SparseVector docVec = (SparseVector) doc.getData();
				int bestClusterNum = clusterNum;
				double distanceToCurrentCentroid =
					this.centroids[clusterNum].distanceSquared(docVec);
				double squareDistanceOfBestCluster = distanceToCurrentCentroid;
				for (int i = 0; i < this.centroids.length; ++i) {
					double distance = 0.0;
					if (clusterNum == i) {
						distance = distanceToCurrentCentroid;
					} else {
						distance = this.centroids[i].distanceSquared(docVec);
					}
					if (distance < squareDistanceOfBestCluster) {
						squareDistanceOfBestCluster = distance;
						bestClusterNum = i;
					}
				}
				if (bestClusterNum != clusterNum) {
					++numReassigned;
				}
				this.newClusters[bestClusterNum].add(doc);
				this.newCentroids[bestClusterNum].plus(docVec);
			}
		}
		for (int i = 0; i < newClusters.length; ++i) {
			this.newCentroids[i].scalarDivide(this.newClusters[i].size());
			this.newQualities[i] = this.calculateClusterQuality(this.newClusters[i],
					this.newCentroids[i]);
			System.out.println("new cluster " + i + " Viarances: " +
					this.newQualities[i] + " Num: "+ newClusters[i].size());
		}
		return (numReassigned);
	}
}