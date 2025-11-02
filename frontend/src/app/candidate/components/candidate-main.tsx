"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import QuestionCard from "./QuestionCard";

interface CandidateMainProps {
  apiBaseUrl: string;
}

interface Question {
  questionId: number;
  title: string;
  tags: string;
  description: string;
  signature: string;
}

export default function CandidateMain({ apiBaseUrl }: CandidateMainProps) {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [err, setErr] = useState<string | null>(null);

  useEffect(() => {
    axios
      .get(`${apiBaseUrl}/questions`)
      .then((res) => setQuestions(res.data))
      .catch((error) => {
        console.error("Error fetching questions:", error);
        setErr(`Failed to load questions: ${error.message}`);
      });
  }, []);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6">Candidate Coding Test</h1>

      {err && (
        <p className="text-red-500">{`Failed calling ${apiBaseUrl}/questions Error: ${err}`}</p>
      )}

      <h1 className="text-2xl font-bold mb-6">Candidate Coding Test</h1>

      {questions.length === 0 ? (
        <p>Loading questions...</p>
      ) : (
        questions.map((q) => <QuestionCard key={q.questionId} question={q} />)
      )}
    </div>
  );
}
