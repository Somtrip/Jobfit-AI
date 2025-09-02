import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaBriefcase, FaBuilding, FaFileAlt, FaSpinner, FaCheck } from 'react-icons/fa';
import axios from 'axios';

const JobDescriptionForm = () => {
  const [formData, setFormData] = useState({
    title: '',
    company: '',
    description: ''
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');
  
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError(''); // Clear error when user types
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title || !formData.company || !formData.description) {
      setError('All fields are required');
      return;
    }
    
    setLoading(true);
    setError('');

    try {
      const response = await axios.post('http://localhost:8080/api/job-descriptions', formData);
      
      setSuccess(true);
      
      // Redirect to dashboard after 2 seconds
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);

    } catch (error) {
      console.error('Error creating job description:', error);
      setError(error.response?.data?.message || 'Failed to create job description. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getWordCount = (text) => {
    return text.trim() ? text.trim().split(/\s+/).length : 0;
  };

  if (success) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center py-12">
        <div className="max-w-md mx-auto px-4 sm:px-6 lg:px-8">
          <div className="bg-white rounded-lg shadow-lg p-8 text-center">
            <div className="flex justify-center mb-4">
              <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
                <FaCheck className="h-8 w-8 text-green-600" />
              </div>
            </div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">
              Job Description Created Successfully!
            </h3>
            <p className="text-gray-600 mb-4">
              Your job description has been analyzed and is ready for matching.
            </p>
            <p className="text-sm text-gray-500">
              Redirecting to dashboard...
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">
            Add Job Description
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Paste or type the job description you want to match against. Our AI will analyze the requirements 
            and help you find the perfect candidate match.
          </p>
        </div>

        {/* Form */}
        <div className="bg-white rounded-lg shadow-lg p-8 mb-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            {error && (
              <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md text-sm">
                {error}
              </div>
            )}

            <div>
              <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-2">
                Job Title *
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <FaBriefcase className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  type="text"
                  id="title"
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                  placeholder="e.g., Senior Software Engineer"
                  required
                />
              </div>
            </div>

            <div>
              <label htmlFor="company" className="block text-sm font-medium text-gray-700 mb-2">
                Company *
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <FaBuilding className="h-5 w-5 text-gray-400" />
                </div>
                <input
                  type="text"
                  id="company"
                  name="company"
                  value={formData.company}
                  onChange={handleChange}
                  className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-primary-500 focus:border-primary-500 sm:text-sm"
                  placeholder="e.g., Tech Corp Inc."
                  required
                />
              </div>
            </div>

            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
                Job Description *
              </label>
              <div className="relative">
                <div className="absolute top-3 left-3 flex items-center pointer-events-none">
                  <FaFileAlt className="h-5 w-5 text-gray-400" />
                </div>
                <textarea
                  id="description"
                  name="description"
                  rows={12}
                  value={formData.description}
                  onChange={handleChange}
                  className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-primary-500 focus:border-primary-500 sm:text-sm resize-none"
                  placeholder="Paste the complete job description here. Include requirements, responsibilities, and qualifications..."
                  required
                />
                <div className="absolute bottom-2 right-2 text-xs text-gray-400">
                  {getWordCount(formData.description)} words
                </div>
              </div>
              <p className="mt-1 text-sm text-gray-500">
                Include detailed requirements, responsibilities, and qualifications for better matching results.
              </p>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={() => navigate('/dashboard')}
                className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-6 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                {loading ? (
                  <span className="flex items-center">
                    <FaSpinner className="animate-spin h-4 w-4 mr-2" />
                    Processing...
                  </span>
                ) : (
                  'Create Job Description'
                )}
              </button>
            </div>
          </form>
        </div>

        {/* Features */}
        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaFileAlt className="h-6 w-6 text-blue-600" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Smart Analysis</h3>
            <p className="text-gray-600 text-sm">
              Our AI automatically extracts key requirements, skills, and qualifications from the job description.
            </p>
          </div>

          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaCheck className="h-6 w-6 text-green-600" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Precise Matching</h3>
            <p className="text-gray-600 text-sm">
              Get accurate candidate matches based on skills, experience, and qualifications alignment.
            </p>
          </div>

          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaSpinner className="h-6 w-6 text-purple-600" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Instant Results</h3>
            <p className="text-gray-600 text-sm">
              Receive detailed matching scores and insights in seconds, not hours.
            </p>
          </div>
        </div>

        {/* Tips */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
          <h3 className="text-lg font-semibold text-blue-900 mb-3">
            💡 Tips for Better Job Descriptions
          </h3>
          <ul className="text-blue-800 space-y-2 text-sm">
            <li>• Include specific technical skills and technologies required</li>
            <li>• List years of experience needed for each requirement</li>
            <li>• Be specific about responsibilities and expectations</li>
            <li>• Include both required and preferred qualifications</li>
            <li>• Mention industry-specific knowledge or certifications</li>
            <li>• Include soft skills and personality traits if relevant</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default JobDescriptionForm; 