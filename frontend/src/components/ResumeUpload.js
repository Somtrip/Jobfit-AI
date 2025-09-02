import React, { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { useNavigate } from 'react-router-dom';
import { FaCloudUploadAlt, FaFilePdf, FaFileWord, FaSpinner, FaCheck, FaTimes } from 'react-icons/fa';
import axios from 'axios';

const ResumeUpload = () => {
  const [uploading, setUploading] = useState(false);
  const [uploadSuccess, setUploadSuccess] = useState(false);
  const [error, setError] = useState('');
  const [uploadedResume, setUploadedResume] = useState(null);
  
  const navigate = useNavigate();

  const onDrop = useCallback(async (acceptedFiles) => {
    if (acceptedFiles.length === 0) return;

    const file = acceptedFiles[0];
    
    // Validate file type
    if (!file.type.includes('pdf') && !file.type.includes('word') && !file.type.includes('document')) {
      setError('Please upload a PDF or Word document');
      return;
    }

    // Validate file size (10MB limit)
    if (file.size > 10 * 1024 * 1024) {
      setError('File size must be less than 10MB');
      return;
    }

    setError('');
    setUploading(true);

    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await axios.post('http://localhost:8080/api/resumes/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      setUploadedResume(response.data);
      setUploadSuccess(true);
      
      // Redirect to dashboard after 2 seconds
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);

    } catch (error) {
      console.error('Upload error:', error);
      setError(error.response?.data?.message || 'Failed to upload resume. Please try again.');
    } finally {
      setUploading(false);
    }
  }, [navigate]);

  const { getRootProps, getInputProps, isDragActive, isDragReject } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document': ['.docx'],
      'application/msword': ['.doc']
    },
    multiple: false
  });

  const getFileIcon = (fileName) => {
    if (fileName.toLowerCase().endsWith('.pdf')) {
      return <FaFilePdf className="h-8 w-8 text-red-500" />;
    }
    return <FaFileWord className="h-8 w-8 text-blue-500" />;
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">
            Upload Your Resume
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Upload your resume in PDF or Word format. Our AI will analyze it and extract key information 
            to help you find the perfect job match.
          </p>
        </div>

        {/* Upload Area */}
        <div className="bg-white rounded-lg shadow-lg p-8 mb-8">
          {!uploadSuccess ? (
            <div
              {...getRootProps()}
              className={`border-2 border-dashed rounded-lg p-12 text-center cursor-pointer transition-colors ${
                isDragActive
                  ? 'border-primary-400 bg-primary-50'
                  : isDragReject
                  ? 'border-red-400 bg-red-50'
                  : 'border-gray-300 hover:border-primary-400 hover:bg-primary-50'
              }`}
            >
              <input {...getInputProps()} />
              
              <div className="space-y-4">
                <div className="flex justify-center">
                  <FaCloudUploadAlt className="h-16 w-16 text-gray-400" />
                </div>
                
                <div>
                  <p className="text-xl font-medium text-gray-900">
                    {isDragActive
                      ? 'Drop your resume here'
                      : 'Drag & drop your resume here'}
                  </p>
                  <p className="text-gray-500 mt-2">
                    or click to browse files
                  </p>
                </div>

                <div className="text-sm text-gray-500">
                  <p>Supported formats: PDF, DOC, DOCX</p>
                  <p>Maximum file size: 10MB</p>
                </div>

                {error && (
                  <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md text-sm">
                    {error}
                  </div>
                )}

                {uploading && (
                  <div className="flex items-center justify-center space-x-2 text-primary-600">
                    <FaSpinner className="animate-spin h-5 w-5" />
                    <span>Processing your resume...</span>
                  </div>
                )}
              </div>
            </div>
          ) : (
            <div className="text-center">
              <div className="flex justify-center mb-4">
                <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center">
                  <FaCheck className="h-8 w-8 text-green-600" />
                </div>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Resume Uploaded Successfully!
              </h3>
              <p className="text-gray-600 mb-4">
                Your resume has been processed and analyzed by our AI.
              </p>
              
              {uploadedResume && (
                <div className="bg-gray-50 rounded-lg p-4 mb-4">
                  <div className="flex items-center justify-center space-x-3">
                    {getFileIcon(uploadedResume.fileName)}
                    <div className="text-left">
                      <p className="font-medium text-gray-900">{uploadedResume.fileName}</p>
                      <p className="text-sm text-gray-500">
                        {uploadedResume.skills?.length || 0} skills detected
                      </p>
                    </div>
                  </div>
                </div>
              )}
              
              <p className="text-sm text-gray-500">
                Redirecting to dashboard...
              </p>
            </div>
          )}
        </div>

        {/* Features */}
        <div className="grid md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaFilePdf className="h-6 w-6 text-blue-600" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Smart Parsing</h3>
            <p className="text-gray-600 text-sm">
              Our AI automatically extracts skills, experience, and education from your resume.
            </p>
          </div>

          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaCheck className="h-6 w-6 text-green-600" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">ATS Optimized</h3>
            <p className="text-gray-600 text-sm">
              Get suggestions to make your resume more ATS-friendly and increase your chances.
            </p>
          </div>

          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaSpinner className="h-6 w-6 text-purple-600" />
            </div>
            <h3 className="text-lg font-semibold text-gray-900 mb-2">Instant Analysis</h3>
            <p className="text-gray-600 text-sm">
              Receive detailed insights and improvement suggestions in seconds.
            </p>
          </div>
        </div>

        {/* Tips */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6">
          <h3 className="text-lg font-semibold text-blue-900 mb-3">
            💡 Tips for Better Results
          </h3>
          <ul className="text-blue-800 space-y-2 text-sm">
            <li>• Ensure your resume is clear and well-formatted</li>
            <li>• Include specific skills and technologies you know</li>
            <li>• Use action verbs to describe your experience</li>
            <li>• Keep your resume up-to-date with recent achievements</li>
            <li>• Use standard section headings (Experience, Skills, Education)</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default ResumeUpload; 