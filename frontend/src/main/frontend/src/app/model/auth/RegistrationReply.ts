import {RegistrationData} from "./RegistrationData";
import {QuestionData} from "./QuestionData";
export class RegistrationReply {
  registrationData: RegistrationData;
  newQuestion: QuestionData;
  code: number;
  errorMsg: string;
}
