"use client";

import { useState } from "react";
import Editor from "@monaco-editor/react";
import axios from "axios";
import { env } from "next-runtime-env";
interface Question {
  questionId: number;
  title: string;
  tags: string;
  description: string;
  signature: string;
}

interface Result {
  passedTestCases: number;
  totalTestCases: number;
  status: "success" | "partial" | "failure";
  executionTime: number;
  message?: string;
}

export default function QuestionCard({ question }: { question: Question }) {
  const [code, setCode] = useState(question.signature);
  const [language, setLanguage] = useState("java");
  const [result, setResult] = useState<Result | null>(null);
  const [loading, setLoading] = useState(false);

  const handleLanguageChange = async (
    e: React.ChangeEvent<HTMLSelectElement>
  ) => {
    const newLang = e.target.value;
    setLanguage(newLang);

    try {
      const res = await axios.get(
        `${env("NEXT_PUBLIC_API_URL")}/questions/${
          question.questionId
        }?language=${newLang}`
      );
      setCode(res.data.signature);
    } catch (err) {
      console.error("Error fetching new signature", err);
    }
  };

  const handleSubmit = async () => {
    setLoading(true);
    setResult(null);

    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_API_URL}/submit`,
        {
          questionId: question.questionId,
          code,
          language,
        }
      );

      const jobId = res.data;

      const interval = setInterval(async () => {
        try {
          const r = await axios.get(
            `${env("NEXT_PUBLIC_API_URL")}/results/${jobId}`
          );
          if (r.status === 200) {
            setResult(r.data);
            clearInterval(interval);
          }
        } catch (error) {
          console.log("Waiting for job to complete...");
        }
      }, 3000);
    } catch (error) {
      console.error("Error submitting code", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="border rounded-lg p-4 mb-6 shadow">
      <h2 className="text-lg font-bold mb-2">{question.title}</h2>
      <p className="mb-2">{question.description}</p>
      <p className="text-sm text-gray-500 mb-3">Tags: {question.tags}</p>

      <select
        className="border p-1 mb-2"
        value={language}
        onChange={handleLanguageChange}
      >
        <option value="java">Java</option>
        <option value="python">Python</option>
      </select>

      <Editor
        height="300px"
        language={language}
        value={code}
        onChange={(value) => setCode(value || "")}
      />

      <button
        className="mt-3 px-4 py-2 bg-blue-500 text-white rounded"
        onClick={handleSubmit}
      >
        Submit
      </button>

      {loading && (
        <p className="mt-2 text-yellow-600 font-semibold">
          Running your code, please wait...
        </p>
      )}

      {result && (
        <div className="mt-4 p-3 border shadow bg-white text-black">
          <p>
            <span>Passed:</span> {result.passedTestCases}/
            {result.totalTestCases}
          </p>
          <p>
            <span>Status:</span>{" "}
            <span
              className={
                result.status === "success"
                  ? "text-green-400"
                  : result.status === "partial"
                  ? "text-yellow-400"
                  : "text-red-400"
              }
            >
              {result.status}
            </span>
          </p>
          <p>
            <span>Execution Time:</span> {result.executionTime} ms
          </p>
          {result.message && (
            <p className="text-red-600">Error: {result.message}</p>
          )}
        </div>
      )}
    </div>
  );
}
