import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { FaFileUpload, FaBriefcase, FaChartBar, FaUser, FaPlus, FaEye } from 'react-icons/fa';
import axios from 'axios';

const Dashboard = () => {
  const { user } = useAuth();
  const [resumes, setResumes] = useState([]);
  const [jobDescriptions, setJobDescriptions] = useState([]);
  const [matchResults, setMatchResults] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [resumesRes, jobDescriptionsRes] = await Promise.all([
        axios.get('http://localhost:8080/api/resumes'),
        axios.get('http://localhost:8080/api/job-descriptions')
      ]);

      setResumes(resumesRes.data);
      setJobDescriptions(jobDescriptionsRes.data);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            {getGreeting()}, {user?.username}! 👋
          </h1>
          <p className="text-gray-600 mt-2">
            Welcome to your JobFit AI dashboard. Here's an overview of your job matching journey.
          </p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-blue-100 text-blue-600">
                <FaFileUpload className="h-6 w-6" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Resumes</p>
                <p className="text-2xl font-semibold text-gray-900">{resumes.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-green-100 text-green-600">
                <FaBriefcase className="h-6 w-6" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Job Descriptions</p>
                <p className="text-2xl font-semibold text-gray-900">{jobDescriptions.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="p-3 rounded-full bg-purple-100 text-purple-600">
                <FaChartBar className="h-6 w-6" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Matches</p>
                <p className="text-2xl font-semibold text-gray-900">{matchResults.length}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
            <div className="space-y-3">
              <Link
                to="/upload-resume"
                className="flex items-center p-3 rounded-lg border border-gray-200 hover:border-primary-300 hover:bg-primary-50 transition-colors"
              >
                <FaPlus className="h-5 w-5 text-primary-600 mr-3" />
                <span className="text-gray-700">Upload New Resume</span>
              </Link>
              <Link
                to="/job-description"
                className="flex items-center p-3 rounded-lg border border-gray-200 hover:border-primary-300 hover:bg-primary-50 transition-colors"
              >
                <FaPlus className="h-5 w-5 text-primary-600 mr-3" />
                <span className="text-gray-700">Add Job Description</span>
              </Link>
              <Link
                to="/matching-results"
                className="flex items-center p-3 rounded-lg border border-gray-200 hover:border-primary-300 hover:bg-primary-50 transition-colors"
              >
                <FaChartBar className="h-5 w-5 text-primary-600 mr-3" />
                <span className="text-gray-700">View Match Results</span>
              </Link>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h3>
            {resumes.length > 0 || jobDescriptions.length > 0 ? (
              <div className="space-y-3">
                {resumes.slice(0, 3).map((resume) => (
                  <div key={resume.id} className="flex items-center p-3 rounded-lg bg-gray-50">
                    <FaFileUpload className="h-4 w-4 text-gray-400 mr-3" />
                    <div className="flex-1">
                      <p className="text-sm font-medium text-gray-900">{resume.fileName}</p>
                      <p className="text-xs text-gray-500">
                        Uploaded {new Date(resume.uploadedAt).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                ))}
                {jobDescriptions.slice(0, 3).map((jd) => (
                  <div key={jd.id} className="flex items-center p-3 rounded-lg bg-gray-50">
                    <FaBriefcase className="h-4 w-4 text-gray-400 mr-3" />
                    <div className="flex-1">
                      <p className="text-sm font-medium text-gray-900">{jd.title}</p>
                      <p className="text-xs text-gray-500">
                        {jd.company} • {new Date(jd.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-sm">No recent activity. Start by uploading a resume or adding a job description!</p>
            )}
          </div>
        </div>

        {/* Recent Resumes */}
        {resumes.length > 0 && (
          <div className="bg-white rounded-lg shadow mb-8">
            <div className="px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900">Recent Resumes</h3>
            </div>
            <div className="divide-y divide-gray-200">
              {resumes.slice(0, 5).map((resume) => (
                <div key={resume.id} className="px-6 py-4 flex items-center justify-between hover:bg-gray-50">
                  <div className="flex items-center">
                    <FaFileUpload className="h-5 w-5 text-gray-400 mr-3" />
                    <div>
                      <p className="text-sm font-medium text-gray-900">{resume.fileName}</p>
                      <p className="text-xs text-gray-500">
                        {resume.skills?.length || 0} skills detected • 
                        Uploaded {new Date(resume.uploadedAt).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                  <Link
                    to={`/resume/${resume.id}`}
                    className="text-primary-600 hover:text-primary-700 text-sm font-medium flex items-center"
                  >
                    <FaEye className="h-4 w-4 mr-1" />
                    View
                  </Link>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Recent Job Descriptions */}
        {jobDescriptions.length > 0 && (
          <div className="bg-white rounded-lg shadow mb-8">
            <div className="px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-semibold text-gray-900">Recent Job Descriptions</h3>
            </div>
            <div className="divide-y divide-gray-200">
              {jobDescriptions.slice(0, 5).map((jd) => (
                <div key={jd.id} className="px-6 py-4 flex items-center justify-between hover:bg-gray-50">
                  <div className="flex items-center">
                    <FaBriefcase className="h-5 w-5 text-gray-400 mr-3" />
                    <div>
                      <p className="text-sm font-medium text-gray-900">{jd.title}</p>
                      <p className="text-xs text-gray-500">
                        {jd.company} • {jd.requiredSkills?.length || 0} required skills • 
                        {new Date(jd.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                  <Link
                    to={`/job-description/${jd.id}`}
                    className="text-primary-600 hover:text-primary-700 text-sm font-medium flex items-center"
                  >
                    <FaEye className="h-4 w-4 mr-1" />
                    View
                  </Link>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Empty State */}
        {resumes.length === 0 && jobDescriptions.length === 0 && (
          <div className="text-center py-12">
            <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaUser className="h-12 w-12 text-gray-400" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">Get Started with JobFit AI</h3>
            <p className="text-gray-500 mb-6">
              Upload your first resume and add a job description to start getting AI-powered matching results.
            </p>
            <div className="flex flex-col sm:flex-row gap-3 justify-center">
              <Link
                to="/upload-resume"
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700"
              >
                <FaFileUpload className="h-4 w-4 mr-2" />
                Upload Resume
              </Link>
              <Link
                to="/job-description"
                className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
              >
                <FaBriefcase className="h-4 w-4 mr-2" />
                Add Job Description
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard; 