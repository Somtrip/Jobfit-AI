import React, { useState, useEffect, useRef } from 'react';
import { FaChartBar, FaLightbulb, FaGraduationCap, FaEye, FaDownload } from 'react-icons/fa';
import { RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, Radar, ResponsiveContainer } from 'recharts';
import axios from 'axios';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

const MatchingResults = () => {
  const [resumes, setResumes] = useState([]);
  const [jobDescriptions, setJobDescriptions] = useState([]);
  const [selectedResume, setSelectedResume] = useState(null);
  const [selectedJobDescription, setSelectedJobDescription] = useState(null);
  const [matchResult, setMatchResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [generatingPDF, setGeneratingPDF] = useState(false);
  
  const reportRef = useRef();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [resumesRes, jobDescriptionsRes] = await Promise.all([
        axios.get('http://localhost:8080/api/resumes'),
        axios.get('http://localhost:8080/api/job-descriptions')
      ]);

      setResumes(resumesRes.data);
      setJobDescriptions(jobDescriptionsRes.data);
    } catch (error) {
      console.error('Error fetching data:', error);
      setError('Failed to fetch data. Please try again.');
    }
  };

  const handleMatch = async () => {
    if (!selectedResume || !selectedJobDescription) {
      setError('Please select both a resume and a job description');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const response = await axios.post('http://localhost:8080/api/matching/match', null, {
        params: {
          resumeId: selectedResume.id,
          jobDescriptionId: selectedJobDescription.id
        }
      });

      setMatchResult(response.data);
    } catch (error) {
      console.error('Error matching:', error);
      setError('Failed to perform matching. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 80) return 'text-green-600';
    if (score >= 60) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getScoreBgColor = (score) => {
    if (score >= 80) return 'bg-green-100';
    if (score >= 60) return 'bg-yellow-100';
    return 'bg-red-100';
  };

  const formatScore = (score) => {
    return Math.round(score * 100);
  };

  const prepareChartData = (skillScores) => {
    if (!skillScores) return [];
    
    return Object.entries(skillScores).map(([skill, score]) => ({
      skill: skill.length > 15 ? skill.substring(0, 15) + '...' : skill,
      score: Math.round(score * 100)
    }));
  };

  // Function to generate and download PDF report
  const generatePDF = () => {
    if (!matchResult) return;
    
    setGeneratingPDF(true);
    
    const input = reportRef.current;
    const pdf = new jsPDF('p', 'mm', 'a4');
    
    html2canvas(input, {
      scale: 2,
      useCORS: true,
      logging: false
    }).then((canvas) => {
      const imgData = canvas.toDataURL('image/png');
      const imgWidth = 210; // A4 width in mm
      const pageHeight = 297; // A4 height in mm
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;
      let position = 0;
      
      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;
      
      // Add more pages if the content is too long
      while (heightLeft >= 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }
      
      pdf.save(`JobFit-Match-Report-${new Date().toISOString().split('T')[0]}.pdf`);
      setGeneratingPDF(false);
    }).catch((error) => {
      console.error('Error generating PDF:', error);
      setGeneratingPDF(false);
      setError('Failed to generate PDF. Please try again.');
    });
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">
            AI-Powered Matching Results
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Select a resume and job description to get instant AI-powered matching results, 
            skill gap analysis, and improvement suggestions.
          </p>
        </div>

        {/* Selection Controls */}
        <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
          <div className="grid md:grid-cols-2 gap-6 mb-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Select Resume
              </label>
              <select
                value={selectedResume?.id || ''}
                onChange={(e) => {
                  const resume = resumes.find(r => r.id === parseInt(e.target.value));
                  setSelectedResume(resume);
                }}
                className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="">Choose a resume...</option>
                {resumes.map((resume) => (
                  <option key={resume.id} value={resume.id}>
                    {resume.fileName}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Select Job Description
              </label>
              <select
                value={selectedJobDescription?.id || ''}
                onChange={(e) => {
                  const jd = jobDescriptions.find(j => j.id === parseInt(e.target.value));
                  setSelectedJobDescription(jd);
                }}
                className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
              >
                <option value="">Choose a job description...</option>
                {jobDescriptions.map((jd) => (
                  <option key={jd.id} value={jd.id}>
                    {jd.title} - {jd.company}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="flex justify-center">
            <button
              onClick={handleMatch}
              disabled={!selectedResume || !selectedJobDescription || loading}
              className="px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center space-x-2"
            >
              <FaChartBar />
              <span>{loading ? 'Analyzing...' : 'Analyze Match'}</span>
            </button>
          </div>

          {error && (
            <div className="mt-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md text-sm">
              {error}
            </div>
          )}
        </div>

        {/* Results Display */}
        {matchResult && (
          <div ref={reportRef} className="space-y-8">
            {/* Overall Score */}
            <div className="bg-white rounded-lg shadow-lg p-8 text-center">
              <h2 className="text-2xl font-bold text-gray-900 mb-4">Overall Match Score</h2>
              <div className="flex justify-center mb-6">
                <div className={`w-32 h-32 rounded-full ${getScoreBgColor(matchResult.overallScore)} flex items-center justify-center`}>
                  <span className={`text-4xl font-bold ${getScoreColor(matchResult.overallScore)}`}>
                    {formatScore(matchResult.overallScore)}%
                  </span>
                </div>
              </div>
              <p className="text-lg text-gray-600">
                {formatScore(matchResult.overallScore) >= 80 ? 'Excellent match!' :
                 formatScore(matchResult.overallScore) >= 60 ? 'Good match with room for improvement' :
                 'Consider improving your resume for better alignment'}
              </p>
            </div>

            {/* Detailed Scores */}
            <div className="grid md:grid-cols-3 gap-6">
              <div className="bg-white rounded-lg shadow p-6 text-center">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Skills Match</h3>
                <div className={`w-20 h-20 rounded-full ${getScoreBgColor(matchResult.skillsScore)} flex items-center justify-center mx-auto mb-3`}>
                  <span className={`text-2xl font-bold ${getScoreColor(matchResult.skillsScore)}`}>
                    {formatScore(matchResult.skillsScore)}%
                  </span>
                </div>
                <p className="text-sm text-gray-600">
                  {formatScore(matchResult.skillsScore) >= 80 ? 'Strong skills alignment' :
                   formatScore(matchResult.skillsScore) >= 60 ? 'Moderate skills match' :
                   'Skills gap identified'}
                </p>
              </div>

              <div className="bg-white rounded-lg shadow p-6 text-center">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Experience Match</h3>
                <div className={`w-20 h-20 rounded-full ${getScoreBgColor(matchResult.experienceScore)} flex items-center justify-center mx-auto mb-3`}>
                  <span className={`text-2xl font-bold ${getScoreColor(matchResult.experienceScore)}`}>
                    {formatScore(matchResult.experienceScore)}%
                  </span>
                </div>
                <p className="text-sm text-gray-600">
                  {formatScore(matchResult.experienceScore) >= 80 ? 'Experience well-aligned' :
                   formatScore(matchResult.experienceScore) >= 60 ? 'Some experience overlap' :
                   'Experience gap identified'}
                </p>
              </div>

              <div className="bg-white rounded-lg shadow p-6 text-center">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Education Match</h3>
                <div className={`w-20 h-20 rounded-full ${getScoreBgColor(matchResult.educationScore)} flex items-center justify-center mx-auto mb-3`}>
                  <span className={`text-2xl font-bold ${getScoreColor(matchResult.educationScore)}`}>
                    {formatScore(matchResult.educationScore)}%
                  </span>
                </div>
                <p className="text-sm text-gray-600">
                  {formatScore(matchResult.educationScore) >= 80 ? 'Education requirements met' :
                   formatScore(matchResult.educationScore) >= 60 ? 'Partial education match' :
                   'Education gap identified'}
                </p>
              </div>
            </div>

            {/* Skills Radar Chart */}
            {matchResult.skillScores && Object.keys(matchResult.skillScores).length > 0 && (
              <div className="bg-white rounded-lg shadow-lg p-8">
                <h3 className="text-xl font-semibold text-gray-900 mb-6 text-center">Skills Breakdown</h3>
                <div className="h-80">
                  <ResponsiveContainer width="100%" height="100%">
                    <RadarChart data={prepareChartData(matchResult.skillScores)}>
                      <PolarGrid />
                      <PolarAngleAxis dataKey="skill" />
                      <PolarRadiusAxis angle={90} domain={[0, 100]} />
                      <Radar
                        name="Skills"
                        dataKey="score"
                        stroke="#3B82F6"
                        fill="#3B82F6"
                        fillOpacity={0.3}
                      />
                    </RadarChart>
                  </ResponsiveContainer>
                </div>
              </div>
            )}

            {/* Missing Skills */}
            {matchResult.missingSkills && matchResult.missingSkills.length > 0 && (
              <div className="bg-white rounded-lg shadow-lg p-8">
                <h3 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <FaLightbulb className="text-yellow-500 mr-2" />
                  Skills to Develop
                </h3>
                <div className="grid md:grid-cols-2 gap-4">
                  {matchResult.missingSkills.map((skill, index) => (
                    <div key={index} className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                      <p className="font-medium text-yellow-800">{skill}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Improvement Suggestions */}
            {matchResult.improvementSuggestions && matchResult.improvementSuggestions.length > 0 && (
              <div className="bg-white rounded-lg shadow-lg p-8">
                <h3 className="text-xl font-semibold text-gray-900 mb-4">Improvement Suggestions</h3>
                <div className="space-y-3">
                  {matchResult.improvementSuggestions.map((suggestion, index) => (
                    <div key={index} className="flex items-start space-x-3">
                      <div className="w-2 h-2 bg-primary-600 rounded-full mt-2 flex-shrink-0"></div>
                      <p className="text-gray-700">{suggestion}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Learning Resources */}
            {matchResult.learningResources && matchResult.learningResources.length > 0 && (
              <div className="bg-white rounded-lg shadow-lg p-8">
                <h3 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <FaGraduationCap className="text-green-500 mr-2" />
                  Learning Resources
                </h3>
                <div className="grid md:grid-cols-2 gap-4">
                  {matchResult.learningResources.map((resource, index) => (
                    <div key={index} className="bg-green-50 border border-green-200 rounded-lg p-4">
                      <p className="text-green-800">{resource}</p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}

        {/* Export Button */}
        {matchResult && (
          <div className="text-center mt-8">
            <button 
              onClick={generatePDF}
              disabled={generatingPDF}
              className="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center space-x-2 mx-auto"
            >
              {generatingPDF ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  <span>Generating PDF...</span>
                </>
              ) : (
                <>
                  <FaDownload />
                  <span>Export Report (PDF)</span>
                </>
              )}
            </button>
          </div>
        )}

        {/* Empty State */}
        {!matchResult && resumes.length > 0 && jobDescriptions.length > 0 && (
          <div className="text-center py-12">
            <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaChartBar className="h-12 w-12 text-gray-400" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">Ready to Analyze?</h3>
            <p className="text-gray-500">
              Select a resume and job description above to get started with AI-powered matching analysis.
            </p>
          </div>
        )}

        {/* No Data State */}
        {(resumes.length === 0 || jobDescriptions.length === 0) && (
          <div className="text-center py-12">
            <div className="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <FaChartBar className="h-12 w-12 text-gray-400" />
            </div>
            <h3 className="text-lg font-medium text-gray-900 mb-2">No Data Available</h3>
            <p className="text-gray-500 mb-6">
              You need to upload at least one resume and create one job description to start matching.
            </p>
            <div className="flex flex-col sm:flex-row gap-3 justify-center">
              <a
                href="/upload-resume"
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-primary-600 hover:bg-primary-700"
              >
                Upload Resume
              </a>
              <a
                href="/job-description"
                className="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
              >
                Add Job Description
              </a>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MatchingResults;