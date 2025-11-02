import CandidateMain from "./components/candidate-main";

export default function CandidatePage() {
  return (
    <div>
      <CandidateMain apiBaseUrl={process.env.NEXT_PUBLIC_API_URL ?? 'localhost'} />
    </div>
  );
}
